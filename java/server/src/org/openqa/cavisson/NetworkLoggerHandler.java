package org.openqa.cavisson;

//import org.openqa.grid.web.servlet.handler.SeleniumBasedRequest;
import javax.servlet.http.HttpServletResponse;
import org.openqa.cavisson.NetworkLogger;
import java.util.logging.Logger;
import java.util.Map;
public class NetworkLoggerHandler{
  private static final Logger log = Logger.getLogger(NetworkLoggerHandler.class.getName());
  private static final String LOGGER_KEY = "networkLogCapture";
  private static final boolean LOG_CAPTURE_INACTIVE = false;
  private static final boolean LOG_CAPTURE_ACTIVE = true;
  private static final String LOG_PAGE_REFERENCE = "initialPageRef";
  private static final String LOG_CAPTURE_HEADER = "captureHeaders";
  private static final String LOG_CAPTURE_COOKIES = "captureCookies" ;
  private static final String LOG_CAPTURE_BINARY_CONTENT = "captureBinaryContent";
  private static final String LOG_CAPTURE_CONTENT = "captureContent";
  private static final String LOG_PAGE_TITLE = "initialPageTitle";
  private static boolean LOG_CAPTURE_FLAG ;
  private int loggingPort;
  private NetworkLogger networkLogger=null;
  public NetworkLoggerHandler(int loggingPort){
    this.loggingPort = loggingPort;
    LOG_CAPTURE_FLAG = LOG_CAPTURE_INACTIVE;
    networkLogger = new NetworkLogger(loggingPort);
    
  }
  public void process(Map<String,Object> body ,HttpServletResponse response)throws Exception{
    log.fine(body.toString());    
    if (body.containsKey(LOGGER_KEY)){
        if (body.get(LOGGER_KEY).toString().equalsIgnoreCase("true") &&  !LOG_CAPTURE_FLAG ){
          String pageRef = "";
          String PageTitle = "";
          boolean captureHeader = true;
          boolean captureCookies = true;
          boolean captureBinaryContent = true;
          boolean captureContent = true;
          LOG_CAPTURE_FLAG = LOG_CAPTURE_ACTIVE;
          if(body.containsKey(LOG_PAGE_REFERENCE)){
            pageRef = body.get(LOG_PAGE_REFERENCE).toString(); 
          }   
          if(body.containsKey(LOG_CAPTURE_HEADER)){
            captureHeader = Boolean.parseBoolean(body.get(LOG_CAPTURE_HEADER).toString()); 
          }   
          if(body.containsKey(LOG_CAPTURE_COOKIES)){
            captureCookies = Boolean.parseBoolean(body.get(LOG_CAPTURE_COOKIES).toString()); 
          }   
          if(body.containsKey(LOG_CAPTURE_BINARY_CONTENT)){
            captureBinaryContent = Boolean.parseBoolean(body.get(LOG_CAPTURE_BINARY_CONTENT).toString()); 
          }   
          if(body.containsKey(LOG_CAPTURE_CONTENT)){
            captureContent = Boolean.parseBoolean(body.get(LOG_CAPTURE_CONTENT).toString()); 
          }   
          if(body.containsKey(LOG_PAGE_TITLE)){
            PageTitle = body.get(LOG_PAGE_TITLE).toString(); 
          }   
          networkLogger.captureNetworkLoggingStart(pageRef,captureHeader,captureCookies,captureBinaryContent,captureContent,PageTitle);
        }
        else if (body.get(LOGGER_KEY).toString().equalsIgnoreCase("false") && LOG_CAPTURE_FLAG){
          networkLogger.captureNetworkLoggingStop(response);    
        } 
      }   
  }
  public void startNetworkLogger()throws Exception{
    networkLogger.startNetworkLogging();
  } 
  public void stopNetworkLogger()throws Exception{
    networkLogger.stopNetworkLogging();
  }
} 
