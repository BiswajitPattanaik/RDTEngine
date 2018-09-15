package org.openqa.cavisson;
import javax.servlet.http.HttpServletResponse;
import okhttp3.OkHttpClient;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import org.openqa.cavisson.HAR;

public class NetworkLogger{
  private int processId;
  private boolean recordingFlag = false ;
  private OkHttpClient client; 
  private int loggingPort;
  private static final String START_NETWORK_LOGGER_URL = "http://127.0.0.1:8991/proxy";
  private static final String STOP_NETWORK_LOGGER_URL = "http://127.0.0.1:8991/proxy/%d";
  private static final String CAPTURING_NETWORK_LOG_URL = "http://127.0.0.1:8991/proxy/%d/har";
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private static final Logger log = Logger.getLogger(NetworkLogger.class.getName());
  public static final MediaType URL_FORM_ENCODED = MediaType.parse("application/x-www-form-urlencoded");
  public NetworkLogger(int loggingPort){
    client = new OkHttpClient();
    this.loggingPort = loggingPort;
  }
  public String startNetworkLogging()throws Exception{
    String requestBody = "port="+loggingPort+"&trustAllServers=true";
    RequestBody body = RequestBody.create(URL_FORM_ENCODED, requestBody);
    Request request = new Request.Builder().url(START_NETWORK_LOGGER_URL).post(body).build();
    Response response = client.newCall(request).execute();
    String responseBody= response.body().string();
    log.fine(" Respone in start Network Logging  ="+responseBody);
    return responseBody.toString(); 
  }
  public String stopNetworkLogging()throws Exception{
    Request request = new Request.Builder().url(String.format(STOP_NETWORK_LOGGER_URL,loggingPort)).delete().build();
    Response response = client.newCall(request).execute();
    String responseBody= response.body().string();
    log.fine("Respone in stop Network Logging"+responseBody);
    return responseBody;
 }
  public void captureNetworkLoggingStart(String pageRef,boolean captureHeader,boolean captureCookies,boolean captureBinaryContent,boolean captureContent,String PageTitle)throws Exception{
    String requestBody =  "captureHeaders="+captureHeader+"&captureCookies="+captureCookies+"&captureContent="+captureContent+"&captureBinaryContent="+captureBinaryContent+"&initialPageRef="+pageRef+"&initialPageTitle="+PageTitle;
    RequestBody body = RequestBody.create(URL_FORM_ENCODED, requestBody);
    Request request = new Request.Builder().url(String.format(CAPTURING_NETWORK_LOG_URL,loggingPort)).put(body).build();
    Response response = client.newCall(request).execute();
    log.fine(" Response after captureNetworkLoggingStart is ="+response.body().string());
  }
  public void captureNetworkLoggingStop(HttpServletResponse httpServletResponse)throws Exception{
    Request request = new Request.Builder().url(String.format(CAPTURING_NETWORK_LOG_URL,loggingPort)).build();
    Response response = client.newCall(request).execute();
    String responseBody= response.body().string();
    //log.fine("Response after captureNetworkLoggingStop is = " +responseBody);
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(responseBody);
    String responseBodyPretty = gson.toJson(je);
    httpServletResponse.setContentType("application/json");
    httpServletResponse.setHeader("Content-Disposition", "attachment; filename=NetworkHarFile");
    //TO:DO No need to send Beautified Har.
    //httpServletResponse.getWriter().write(responseBodyPretty);
    //TO::DO Enhancement , sending HAR Message with har response 
    HAR har = new HAR(responseBody);
    //log.fine("HAR Message = "+har.getHarMessage());
    httpServletResponse.getWriter().write(har.getHarMessage());
    httpServletResponse.getWriter().flush();
    httpServletResponse.getWriter().close();
  }
  public void startLogEngine(String logEnginePath)throws Exception{
    long startTime=System.currentTimeMillis();
    String logEngineCommand="/bin/bash "+logEnginePath;
    Process p = Runtime.getRuntime().exec(logEngineCommand);
    p.waitFor();
    BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line = "";
    String output = "";
    while ((line = buf.readLine()) != null) {
      output = output+""+line;
    }
    log.fine("Starting network log Engine with PID = "+output);
    processId = Integer.parseInt(output);
    recordingFlag = true ;
    log.fine("Log Engine Started in "+((long)System.currentTimeMillis() - startTime));
  }
  public void stopLogEngine()throws Exception {
    String cmd;
    cmd =  "kill -9 "+processId;
    if(recordingFlag){
      Process p = Runtime.getRuntime().exec(cmd);
      p.waitFor();
      log.fine("Log Engine Stopped in ");
    }
  }
  public void appendNetWorkLoggingTimeOut(){

  }
}
