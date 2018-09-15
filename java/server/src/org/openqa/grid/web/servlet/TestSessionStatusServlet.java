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

package org.openqa.grid.web.servlet;

import static org.openqa.selenium.json.Json.MAP_TYPE;

import com.google.common.collect.ImmutableMap;

import org.openqa.grid.common.exception.GridException;
import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.RemoteProxy;
import org.openqa.grid.internal.TestSession;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.json.JsonException;
import org.openqa.selenium.json.JsonInput;
import org.openqa.selenium.json.JsonOutput;
import javax.servlet.ServletOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.File;
import java.io.ByteArrayOutputStream; 
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestSessionStatusServlet extends RegistryBasedServlet {
  private final String WORKING_DIR_NAME = "WORKING_DIR";
  private final Json json = new Json();
  private static final Logger log = Logger.getLogger(TestSessionStatusServlet.class.getName());
  private String currentWorkingDir = null ;
  public TestSessionStatusServlet() {
    super(null);
    try{
    currentWorkingDir = createWorkingDir();
    }catch(Exception e){log.fine("Exception in TestSessionServlet Initialization");}
  }

  public TestSessionStatusServlet(GridRegistry registry) {
    super(registry);
  }
  private String createWorkingDir() throws Exception{
    File workingDir=null;
      String currentWorkingDir = new File(".").getAbsolutePath();
      String workingDirPath = currentWorkingDir+"/"+WORKING_DIR_NAME;
      workingDir = new File(workingDirPath);
      if (!workingDir.exists()){
        boolean result = workingDir.mkdir();
        if (!result){
          throw new NullPointerException("Not able to Create Working Dir hence existing");
        }
      }
    return workingDir.getAbsolutePath();
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    log.fine("writing the http servelte response");
    Map<String, Object> json = ImmutableMap.of(
        "session", "abcd");
    /*Map<String, Object> json = ImmutableMap.of(
        "session", request.getParameter("session"));*/
    //process(response, json);
    process(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    Map<String, Object> requestJSON = new HashMap<>();
    if (request.getInputStream() != null) {
      try (Reader rd = new BufferedReader(new InputStreamReader(request.getInputStream()));
           JsonInput jin = json.newInput(rd)) {
        requestJSON = jin.read(MAP_TYPE);
      }
    }
    process(response, requestJSON);
  }
  protected void process(HttpServletRequest request, HttpServletResponse response)throws IOException{
      PrintWriter out = null;
      //out.println("this is just response ");
      log.fine("writing the http servelte response");
      log.fine("writing the working dir = "+currentWorkingDir);
      log.fine("path info = "+request.getPathInfo());
      String requestPathInfo = request.getPathInfo(); 
      File currentWorkingDirectory = new File(currentWorkingDir);
      String sessionId = null;
      String artifactName = null;
      try{
      sessionId = requestPathInfo.split("/")[1];
      }catch(Exception ae){sessionId = "";}
      try{
      artifactName = requestPathInfo.split("/")[2];
      }catch(Exception ae){artifactName = "";}
      File sessionDir = null;
      File artifactFile = null;
      if (sessionId.equals("")){
        log.fine("List the available sessions");
        out = new PrintWriter(response.getWriter());
        File[] fList = currentWorkingDirectory.listFiles();
        for (File file : fList){
          out.println(file.getName());
        }
      }
      else if (!sessionId.equals("") && artifactName.equals("")){
        sessionDir = new File(currentWorkingDir+"/"+sessionId);	
        out = new PrintWriter(response.getWriter());
        if( sessionDir.exists() && sessionDir.isDirectory()){
          response.setStatus(200);
          log.fine("request session id ="+sessionId);
          log.fine("List the available artifacts in the session ");
          File[] fList = sessionDir.listFiles();
          for (File file : fList){
            out.println(file.getName());
          }
        }
        else{
          response.setStatus(404);
          out.println("NOT FOUND");
        }
      }
      else if(!sessionId.equals("") && !artifactName.equals("")){
        artifactFile = new File(currentWorkingDir+"/"+sessionId+"/"+artifactName);
        log.fine("artifact file name = "+artifactFile.getAbsolutePath());
        if(artifactFile.exists() && artifactFile.isFile()){
          log.fine("get the artifact");
            byte[] body = getBytesFromFile(artifactFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(body.length);
            baos.write(body, 0, body.length);
            //response.setContentType("video/mp4");
            response.setContentLength(body.length);
            response.setHeader("Content-Disposition", "inline; filename="+ artifactName +";");
            ServletOutputStream servletOutputStream = response.getOutputStream();
            baos.writeTo(servletOutputStream);
            baos.close();
            servletOutputStream.flush();
            servletOutputStream.close();

	}
        else{
          response.setStatus(404);
          out.println("NOT FOUND");
        }
      }
  }
  protected void process(HttpServletResponse response, Map<String, Object> requestJson)
      throws IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.setStatus(200);
    try (Writer writer = response.getWriter();
      //   JsonOutput out = json.newOutput(writer)) {
      //out.write(getResponse(requestJson));
      PrintWriter out = new PrintWriter(writer)){
      out.println("this is just response ");
      log.fine("writing the http servelte response");
      //out.write(getResponse(requestJson));
    } catch (JsonException e) {
      throw new GridException(e.getMessage());
    }
  }

  private Map<String, Object> getResponse(Map<String, Object> requestJson) {
    Map<String, Object> res = new TreeMap<>();
    res.put("success", false);

    // the id can be specified via a param, or in the json request.
    String session;
    if (!requestJson.containsKey("session")) {
      res.put(
          "msg",
          "you need to specify at least a session or internalKey when call the test slot status service.");
      return res;
    }
      session = String.valueOf(requestJson.get("session"));

    TestSession testSession = getRegistry().getSession(ExternalSessionKey.fromString(session));

    if (testSession == null) {
      res.put("msg", "Cannot find test slot running session " + session + " in the registry.");
      return res;
    }
    res.put("msg", "slot found !");
    res.remove("success");
    res.put("success", true);
    res.put("session", testSession.getExternalKey().getKey());
    res.put("internalKey", testSession.getInternalKey());
    res.put("inactivityTime", testSession.getInactivityTime());
    RemoteProxy p = testSession.getSlot().getProxy();
    res.put("proxyId", p.getId());
    return res;
  }
public byte[] getBytesFromFile(File file) throws IOException {
        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new IOException("File is too large!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        return bytes;
    }

}

