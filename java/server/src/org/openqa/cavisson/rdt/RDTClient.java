package org.openqa.cavisson.rdt;


import com.google.gson.GsonBuilder;
import java.util.logging.Logger;
import org.openqa.cavisson.AdbCommandExecutor;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.util.Map;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class RDTClient{
  private static final Logger log = Logger.getLogger(RDTClient.class.getName());
  private AdbCommandExecutor adbCommandExecutor = null ;
  private final String JSON_SESSION_ID_KEY = "server:CONFIG_UUID" ;
  private final String WEB_STORAGE_ENABLED = "webStorageEnabled" ;
  private final String TAKE_SCREENSHOT = "takesScreenshot" ;
  private final String JAVA_SCRIPT_ENABLED = "javascriptEnabled" ;
  private final String DATABASE_ENABLED = "databaseEnabled" ;
  private final String NETWORK_CONNECTION_ENABLED = "networkConnectionEnabled" ;
  private final String LOCATION_CONTEXT_ENABLED = "locationContextEnabled" ;
  private final String PLATFORM_NAME = "platformName" ;
  private final String APP_ACTIVITY = "appActivity" ;
  private final String APP_PACKAGE = "appPackage" ;
  private final String DEVICE_NAME = "deviceName" ;
  private final String DEVICE_VERSION = "version" ;
  private final String DEVICE_UDID = "deviceUDID" ;
  private final String PLATFORM_VERSION = "platformVersion" ;
  private final String DEVICE_SCREEN_SIZE = "deviceScreenSize" ;
  private final String DEVICE_MODEL = "deviceModel" ;
  private final String DEVICE_MANUFACTURER = "deviceManufacturer"; 
  private final String RDT_SESSION_ID = "sessionId"; 
  private Gson gson ;
  private JsonParser jsonParser ;
  private String sessionKey ;
  private String packageName;
  private Socket socket;
  private Map<String,Object> desiredCapabilities ;
  private Map<String,Object> extraCapabilities ;
  public RDTClient()throws Exception{
    adbCommandExecutor = new AdbCommandExecutor();
    adbCommandExecutor.tcpForward(4724,4724);
    gson = new Gson();
    jsonParser = new JsonParser();
    socket=new Socket("localhost",4724);
    socket.setKeepAlive(true);
  }
  public RDTClient(String deviceId)throws Exception{
    adbCommandExecutor = new AdbCommandExecutor(deviceId);
    adbCommandExecutor.tcpForward(4724,4724);
    gson = new Gson();
    jsonParser = new JsonParser();
    socket=new Socket("localhost",4724);
    socket.setKeepAlive(true);
  }
  public String createSession(RDTBasedRequest rdtBasedRequest){
    try{
      desiredCapabilities = rdtBasedRequest.getDesiredCapabilities();
      extraCapabilities = rdtBasedRequest.getExtraCapabilities();
      String method = rdtBasedRequest.getMethod();
      String requestPath = rdtBasedRequest.getRequestPath();
      String requestBody = rdtBasedRequest.getRequestBody();
      boolean newSessionRequest = rdtBasedRequest.getNewSessionrequest(); 
      log.fine(" desired capabilities "+ desiredCapabilities + " Extra Capabilities " + extraCapabilities  + " Method " + method  + "Request Path" + requestPath  + " New SessionRequest " + newSessionRequest + " Request Body " + requestBody);
      packageName = desiredCapabilities.get(APP_PACKAGE).toString();
      String releaseVersion = adbCommandExecutor.getReleaseVersion();
      String buildVersion = adbCommandExecutor.getBuildVersion();
      String manufacturer = adbCommandExecutor.getManufacturer();
      String model = adbCommandExecutor.getModel();
      String windowSize = adbCommandExecutor.getWindowSize();
      String platform = desiredCapabilities.containsKey("platform")?desiredCapabilities.get("platform").toString():"";
      String deviceName = adbCommandExecutor.getDeviceName();      
      boolean isPackageAvailable = adbCommandExecutor.isPackageAvailableOnDevice(packageName);
      boolean launchApplicationByName = adbCommandExecutor.launchApplicationByPackageName(packageName,desiredCapabilities.get(APP_ACTIVITY).toString()); 
      //Save Session ID for future Use
      sessionKey = desiredCapabilities.get(JSON_SESSION_ID_KEY).toString(); 
      log.fine(" Release Version = " + releaseVersion+ " SDK Version  = " + buildVersion + " Manufacturer  = " + manufacturer +" Model = " + model+" Window Size = " + windowSize+" Is Package Available on Device =" + isPackageAvailable+" Starting the package on Device =" + launchApplicationByName); 
      //Create response for session success 
      JsonObject jsonObject = new JsonObject();
      JsonObject jsonValue = new JsonObject();
      JsonObject desiredJsonCapability =(JsonObject)jsonParser.parse(gson.toJson(rdtBasedRequest.getDesiredCapabilities()));
      JsonObject jsonCapability = new JsonObject();
      JsonObject jsonWarning = new JsonObject();
      jsonCapability.add("desired",(JsonElement)desiredJsonCapability);
      jsonCapability.add("warnings",(JsonElement)jsonWarning);
      jsonCapability.addProperty(WEB_STORAGE_ENABLED,true);
      jsonCapability.addProperty(TAKE_SCREENSHOT,true);
      jsonCapability.addProperty(JAVA_SCRIPT_ENABLED,true);
      jsonCapability.addProperty(DATABASE_ENABLED,true);
      jsonCapability.addProperty(NETWORK_CONNECTION_ENABLED,true);
      jsonCapability.addProperty(LOCATION_CONTEXT_ENABLED,true);
      for (String key : desiredJsonCapability.keySet()){
        jsonCapability.addProperty(key,desiredJsonCapability.get(key).getAsString());
      }
      jsonCapability.addProperty(DEVICE_NAME,deviceName);
      jsonCapability.addProperty(DEVICE_UDID,deviceName);
      jsonCapability.addProperty(DEVICE_SCREEN_SIZE,windowSize);
      jsonCapability.addProperty(DEVICE_MODEL,model);
      jsonCapability.addProperty(DEVICE_MANUFACTURER,manufacturer);
      jsonCapability.addProperty(PLATFORM_VERSION,releaseVersion);
      jsonValue.add("capabilities",(JsonElement)jsonCapability);
      jsonValue.addProperty("sessionId",desiredCapabilities.get(JSON_SESSION_ID_KEY).toString());
      
      jsonObject.add("value",(JsonElement)jsonValue);
      log.fine(jsonObject.toString());
      return jsonObject.toString();
    }catch(Exception e){log.fine(" Exception Caught "+e.getMessage());return "";}

  }
  public String execute(RDTBasedRequest rdtBasedRequest){
    if(rdtBasedRequest.getNewSessionrequest()){
      return createSession(rdtBasedRequest);      
    }
    else{
      String method = rdtBasedRequest.getMethod();
      String requestPath = rdtBasedRequest.getRequestPath();
      boolean newSessionRequest = rdtBasedRequest.getNewSessionrequest(); 
      String requestBody = rdtBasedRequest.getRequestBody();
      log.fine(" ********desired capabilities "+ desiredCapabilities + " Extra Capabilities " + extraCapabilities  + " Method " + method  + "Request Path" + requestPath  + " New SessionRequest " + newSessionRequest + " Request Body " + requestBody);
      if (method.equals("DELETE")){
        log.fine("Received request to delete session i.e DELETE Request "+requestPath.split("/")[2]+" "+sessionKey);
        if(requestPath.split("/")[2].equals(sessionKey)){
          log.fine("Received request to delete session "+sessionKey);
          return quit();
        }
      }
      else if(method.equals("POST")){
        if(requestPath.endsWith("element")){
          if(requestPath.split("/")[2].equals(sessionKey)){
            return findElement(requestBody);
          }
        }
      }
      return "";
    }
  }
  public String quit(){
    try{
      adbCommandExecutor.stopUiAutiomatorLogger();
      adbCommandExecutor.stopUiAutomator();
      adbCommandExecutor.stopApplicationByPackageName(packageName);
    }catch(Exception e){log.fine(" Exception Caught "+e.getMessage());return "";}
    return ""; 
  }
  public String findElement(String requestBody){
    log.fine(" FindElement requested");
    JsonObject elementJson = (JsonObject)jsonParser.parse(requestBody.replaceAll("(\n|\t)",""));
    String selector = null;
    String strategy = null;
    String using = elementJson.get("using").getAsString();
    String value = elementJson.get("value").getAsString();
    log.fine(" [ FindElement ] USING " + using);
    log.fine(" [ FindElement ] VALUE " + value);
    if(using.equals("xpath")){
      strategy = "xpath";
      selector = value; 
    }
    else if(using.equals("css selector")){
      if(value.startsWith(".")){
        strategy = "className";
        selector = value.replaceAll("\\\\", "").substring(1);
      }
      else if(value.startsWith("#")){
        strategy = "id";
        selector = value.replaceAll("\\\\", "").substring(1); 
      }
    }
    JsonObject jsonRequest = new JsonObject();
    jsonRequest.addProperty("cmd","action");
    jsonRequest.addProperty("action","find");
    JsonObject params = new JsonObject();
    params.addProperty("strategy",strategy);
    params.addProperty("selector",selector);
    params.addProperty("context","");
    params.addProperty("multiple",true);
    jsonRequest.add("params",(JsonElement)params);
    log.fine(" Json Request = "+jsonRequest.toString());
    String output = sendDataToBootstrap(jsonRequest.toString());
    log.fine(" Json Response = "+output);
    return output;
  }
  public String sendDataToBootstrap(String requestBody){
    String outPut=null;
    try{
      DataOutputStream dout=new DataOutputStream(socket.getOutputStream()); 
      dout.write(new String(requestBody+"\n").getBytes());
      BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));   
      while(!br.ready()) {
       //log.fine("inside loop");	
      }
      log.fine("outside loop");	
      outPut = br.readLine();
    }catch(Exception e){log.fine("Exception Caught " + e.getMessage());e.printStackTrace();}
    return outPut;
  }
}
