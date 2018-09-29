// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.grid.internal;

import net.jcip.annotations.ThreadSafe;

import org.openqa.grid.internal.listeners.RegistrationListener;
import org.openqa.grid.internal.listeners.SelfHealingProxy;
import org.openqa.grid.internal.utils.configuration.GridHubConfiguration;
import org.openqa.grid.internal.utils.configuration.GridNodeConfiguration;
import org.openqa.grid.web.Hub;
import org.openqa.grid.web.servlet.handler.RequestHandler;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.server.log.LoggingManager;

import org.openqa.grid.common.SeleniumProtocol;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Kernel of the grid. Keeps track of what's happening, what's free/used and assigns resources to
 * incoming requests.
 */
@ThreadSafe
public class DefaultGridRegistry extends BaseGridRegistry implements GridRegistry {
  private static final Logger LOG = Logger.getLogger(DefaultGridRegistry.class.getName());

  protected static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
      LOG.log(Level.SEVERE, "Matcher thread dying due to unhandled exception.", e);
    }
  }

  // lock for anything modifying the tests session currently running on this
  // registry.
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition testSessionAvailable = lock.newCondition();
  private final ProxySet proxies;
  private final ActiveTestSessions activeTestSessions = new ActiveTestSessions();

  private final NewSessionRequestQueue newSessionQueue;
  private final Matcher matcherThread = new Matcher();
  private final List<RemoteProxy> registeringProxies = new CopyOnWriteArrayList<>();

  private volatile boolean stop = false;

  public DefaultGridRegistry() {
    this(null);
  }

  public DefaultGridRegistry(Hub hub) {
    super(hub);
    this.newSessionQueue = new NewSessionRequestQueue();
    proxies = new ProxySet((hub != null) ? hub.getConfiguration().throwOnCapabilityNotPresent : true);
    this.matcherThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
  }

  public void start() {
    matcherThread.start();

    // freynaud : TODO
    // Grid registry is in a valid state when testSessionAvailable.await(); from
    // assignRequestToProxy is reached. Not before.
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a new {@link GridRegistry} and starts it.
   *
   * @param hub the {@link Hub} to associate this registry with
   * @return the registry
   */
  public static GridRegistry newInstance(Hub hub) {
    DefaultGridRegistry registry = new DefaultGridRegistry(hub);
    registry.start();
    return registry;
  }

  /**
   * Ends this test session for the hub, releasing the resources in the hub / registry. It does not
   * release anything on the remote. The resources are released in a separate thread, so the call
   * returns immediately. It allows release with long duration not to block the test while the hub is
   * releasing the resource.
   *
   * @param session The session to terminate
   * @param reason  the reason for termination
   */
  public void terminate(final TestSession session, final SessionTerminationReason reason) {
    // Thread safety reviewed
    new Thread(() -> _release(session.getSlot(), reason)).start();
  }

  /**
   * Release the test slot. Free the resource on the slot itself and the registry. If also invokes
   * the {@link org.openqa.grid.internal.listeners.TestSessionListener#afterSession(TestSession)} if
   * applicable.
   *
   * @param testSlot The slot to release
   */
  private void _release(TestSlot testSlot, SessionTerminationReason reason) {
    if (!testSlot.startReleaseProcess()) {
      return;
    }

    if (!testSlot.performAfterSessionEvent()) {
      return;
    }

    final String internalKey = testSlot.getInternalKey();

    try {
      lock.lock();
      testSlot.finishReleaseProcess();
      release(internalKey, reason);
    } finally {
      lock.unlock();
    }
  }

  void terminateSynchronousFOR_TEST_ONLY(TestSession testSession) {
    _release(testSession.getSlot(), SessionTerminationReason.CLIENT_STOPPED_SESSION);
  }

  /**
   * @see GridRegistry#removeIfPresent(RemoteProxy)
   */
  public void removeIfPresent(RemoteProxy proxy) {
    // Find the original proxy. While the supplied one is logically equivalent, it may be a fresh object with
    // an empty TestSlot list, which doesn't figure into the proxy equivalence check.  Since we want to free up
    // those test sessions, we need to operate on that original object.
    if (proxies.contains(proxy)) {
      LOG.warning(String.format(
          "Cleaning up stale test sessions on the unregistered node %s", proxy));

      final RemoteProxy p = proxies.remove(proxy);
      p.getTestSlots().forEach(testSlot -> forceRelease(testSlot, SessionTerminationReason.PROXY_REREGISTRATION) );
      p.teardown();
    }
  }

  /**
   * @see GridRegistry#forceRelease(TestSlot, SessionTerminationReason)
   */
  public void forceRelease(TestSlot testSlot, SessionTerminationReason reason) {
    if (testSlot.getSession() == null) {
      return;
    }

    String internalKey = testSlot.getInternalKey();
    release(internalKey, reason);
    testSlot.doFinishRelease();
  }


  /**
   * iterates the queue of incoming new session request and assign them to proxy after they've been
   * sorted by priority, with priority defined by the prioritizer.
   */
  class Matcher extends Thread { // Thread safety reviewed

    Matcher() {
      super("Matcher thread");
    }

    @Override
    public void run() {
      try {
        lock.lock();
        assignRequestToProxy();
      } finally {
        lock.unlock();
      }
    }

  }

  /**
   * @see GridRegistry#stop()
   */
  public void stop() {
    stop = true;
    matcherThread.interrupt();
    newSessionQueue.stop();
    proxies.teardown();
  }

  /**
   * @see GridRegistry#addNewSessionRequest(RequestHandler)
   */
  public void addNewSessionRequest(RequestHandler handler) {
    try {
      lock.lock();
      // Added By Biswajit to Design new RDT Architecture .
      //if(handler.getRequest().getDesiredCapabilities().get("automationName").toString().equalsIgnoreCase("RDT")){
      if(handler.getRequest().getDesiredCapabilities().containsKey("automationName")?handler.getRequest().getDesiredCapabilities().get("automationName").toString().equalsIgnoreCase("RDT"):false){
        proxies.setThrowOnCapabilityNotPresent(false);
        LOG.fine("automation RDT Found "+handler.getRequest().getDesiredCapabilities());
      }
      proxies.verifyAbilityToHandleDesiredCapabilities(handler.getRequest().getDesiredCapabilities());
      newSessionQueue.add(handler);
      fireMatcherStateChanged();
    } finally {
      lock.unlock();
      LOG.fine("Add New Session lock released");
    }

  }

  /**
   * iterates the list of incoming session request to find a potential match in the list of proxies.
   * If something changes in the registry, the matcher iteration is stopped to account for that
   * change.
   */
  private void assignRequestToProxy() {
    while (!stop) {
      try {
        testSessionAvailable.await(5, TimeUnit.SECONDS);

        newSessionQueue.processQueue(
            this::takeRequestHandler,
            getHub().getConfiguration().prioritizer);
        // Just make sure we delete anything that is logged on this thread from memory
        LoggingManager.perSessionLogHandler().clearThreadTempLogs();
        LOG.info("Assign Request To Proxy Done");
      } catch (InterruptedException e) {
        LOG.info("Shutting down registry.");
      } catch (Throwable t) {
        LOG.log(Level.SEVERE, "Unhandled exception in Matcher thread.", t);
      }
    }

  }

  private boolean takeRequestHandler(RequestHandler handler) {
    final TestSession session = proxies.getNewSession(handler.getRequest().getDesiredCapabilities());
    //Added By Biswajit to handle sessioncreated = false when automationName != "RDT"
    final boolean sessionCreated = (session != null);
    if (!sessionCreated && (handler.getRequest().getDesiredCapabilities().containsKey("automationName")?handler.getRequest().getDesiredCapabilities().get("automationName").equals("RDT"):false)) {
      RemoteProxy p1 = null;
      try{
      p1 = RemoteProxyFactory.getNewBasicRemoteProxy(new HashMap(), "http://localhost:4444", this);
      }catch(Exception e){LOG.fine(" Exception Caunght"+e.getMessage());}
      this.add(p1);
      HashMap<String,Object> capabilities = new HashMap<String,Object>();
      capabilities.put(GridNodeConfiguration.CONFIG_UUID_CAPABILITY,UUID.randomUUID().toString());
      TestSlot testSlot = new TestSlot(p1, SeleniumProtocol.Selenium, "", capabilities);
      Map<String,Object> desiredCapabilities = handler.getRequest().getDesiredCapabilities(); 
      desiredCapabilities.put(GridNodeConfiguration.CONFIG_UUID_CAPABILITY,capabilities.get(GridNodeConfiguration.CONFIG_UUID_CAPABILITY)); 
      TestSession testSession = new TestSession(testSlot, desiredCapabilities, new Clock(){
      private long time = 17;
      @Override
      public Instant instant() {
        return Instant.ofEpochMilli(time);
      }

      @Override
      public ZoneId getZone() {
        return ZoneId.systemDefault();
      }

      @Override
      public Clock withZone(ZoneId zone) {
        return this;
      }
      });
      testSession.setExternalKey(new ExternalSessionKey(capabilities.get(GridNodeConfiguration.CONFIG_UUID_CAPABILITY).toString()));
      activeTestSessions.add(testSession);
      handler.bindSession(testSession);
      return true ;
    }
    else if(sessionCreated) {
      activeTestSessions.add(session);
      handler.bindSession(session);
      return sessionCreated;
    }
    else{
      return false;
    }
  }

  /**
   * mark the session as finished for the registry. The resources that were associated to it are now
   * free to be reserved by other tests
   *
   * @param session The session
   * @param reason  the reason for the release
   */
  private void release(TestSession session, SessionTerminationReason reason) {
    try {
      lock.lock();
      boolean removed = activeTestSessions.remove(session, reason);
      if (removed) {
        fireMatcherStateChanged();
      }
    } finally {
      lock.unlock();
    }
  }

  private void release(String internalKey, SessionTerminationReason reason) {
    if (internalKey == null) {
      return;
    }
    final TestSession session1 = activeTestSessions.findSessionByInternalKey(internalKey);
    if (session1 != null) {
      release(session1, reason);
      return;
    }
    LOG.warning("Tried to release session with internal key " + internalKey +
                " but couldn't find it.");
  }

  /**
   * @see GridRegistry#add(RemoteProxy)
   */
  public void add(RemoteProxy proxy) {
    if (proxy == null) {
      return;
    }
    LOG.info("Registered a node " + proxy);
    try {
      lock.lock();

      removeIfPresent(proxy);

      if (registeringProxies.contains(proxy)) {
        LOG.warning(String.format("Proxy '%s' is already queued for registration.", proxy));

        return;
      }

      registeringProxies.add(proxy);
      fireMatcherStateChanged();
    } finally {
      lock.unlock();
    }

    boolean listenerOk = true;
    try {
      if (proxy instanceof RegistrationListener) {
        ((RegistrationListener) proxy).beforeRegistration();
      }
    } catch (Throwable t) {
      LOG.severe("Error running the registration listener on " + proxy + ", " + t.getMessage());
      t.printStackTrace();
      listenerOk = false;
    }

    try {
      lock.lock();
      registeringProxies.remove(proxy);
      if (listenerOk) {
        if (proxy instanceof SelfHealingProxy) {
          ((SelfHealingProxy) proxy).startPolling();
        }
        proxies.add(proxy);
        fireMatcherStateChanged();
      }
    } finally {
      lock.unlock();
    }

  }

  /**
   * @see GridRegistry#setThrowOnCapabilityNotPresent(boolean)
   */
  public void setThrowOnCapabilityNotPresent(boolean throwOnCapabilityNotPresent) {
    proxies.setThrowOnCapabilityNotPresent(throwOnCapabilityNotPresent);
  }

  private void fireMatcherStateChanged() {
    testSessionAvailable.signalAll();
  }

  /**
   * @see GridRegistry#getAllProxies()
   */
  public ProxySet getAllProxies() {
    return proxies;
  }

  /**
   * @see GridRegistry#getUsedProxies()
   */
  public List<RemoteProxy> getUsedProxies() {
    return proxies.getBusyProxies();
  }

  /**
   * @see GridRegistry#getSession(ExternalSessionKey)
   */
  public TestSession getSession(ExternalSessionKey externalKey) {
    return activeTestSessions.findSessionByExternalKey(externalKey);
  }

  /**
   * @see GridRegistry#getExistingSession(ExternalSessionKey)
   */
  public TestSession getExistingSession(ExternalSessionKey externalKey) {
    return activeTestSessions.getExistingSession(externalKey);
  }

  /**
   * @see GridRegistry#getNewSessionRequestCount()
   */
  public int getNewSessionRequestCount() {
    // may race
    return newSessionQueue.getNewSessionRequestCount();
  }

  /**
   * @see GridRegistry#clearNewSessionRequests()
   */
  public void clearNewSessionRequests() {
    newSessionQueue.clearNewSessionRequests();
  }

  /**
   * @see GridRegistry#removeNewSessionRequest(RequestHandler)
   */
  public boolean removeNewSessionRequest(RequestHandler request) {
    return newSessionQueue.removeNewSessionRequest(request);
  }

  /**
   * @see GridRegistry#getDesiredCapabilities()
   */
  public Iterable<DesiredCapabilities> getDesiredCapabilities() {
    return newSessionQueue.getDesiredCapabilities();
  }

  /**
   * @see GridRegistry#getActiveSessions()
   */
  public Set<TestSession> getActiveSessions() {
    return activeTestSessions.unmodifiableSet();
  }

  /**
   * @see GridRegistry#getProxyById(String)
   */
  public RemoteProxy getProxyById(String id) {
    return proxies.getProxyById(id);
  }

}
