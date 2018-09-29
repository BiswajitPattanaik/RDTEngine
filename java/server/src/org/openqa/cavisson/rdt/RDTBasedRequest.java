package org.openqa.cavisson.rdt;
import java.util.HashMap;

public class RDTBasedRequest{
  private HashMap<String,Object> desiredCapabilities = null;
  private String requestBody ;
  private HashMap<String,Object> extraCapability = null;
  private String method = null;
  private String requestPath = null ;
  private boolean newSessionrequest ;
  public RDTBasedRequest(HashMap<String,Object> desiredCapabilities ,HashMap<String,Object> extraCapability ,String method , String requestPath,boolean newSessionrequest,String requestBody){
    this.desiredCapabilities = desiredCapabilities;
    this.extraCapability = extraCapability;
    this.method = method ;
    this.requestPath= requestPath;
    this.newSessionrequest = newSessionrequest;
    this.requestBody = requestBody;
  }
  public void setDesiredCapabilities(HashMap<String,Object> desiredCapabilities){
    this.desiredCapabilities = desiredCapabilities;
  }
  public HashMap<String,Object> getDesiredCapabilities(){
    return desiredCapabilities;
  }
  public void setExtraCapabilities(HashMap<String,Object> extraCapability){
    this.extraCapability = extraCapability;
  }
  public HashMap<String,Object> getExtraCapabilities(){
    return this.extraCapability;
  } 
  public void setMethod(String method){
    this.method = method;
  }
  public String getMethod(){
    return this.method;
  }
  public void setRequestPath(String requestPath){
    this.requestPath = requestPath;
  }
  public String getRequestPath(){
    return this.requestPath;
  }
  public void setNewSessionrequest(boolean newSessionrequest){
    this.newSessionrequest = newSessionrequest;
  }
  public boolean getNewSessionrequest(){
    return this.newSessionrequest;
  }
  public void setRequestBody(String requestBody){
    this.requestBody = requestBody;
  }
  public String getRequestBody(){
    return this.requestBody;
  }
}
