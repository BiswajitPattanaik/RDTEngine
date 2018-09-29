package org.openqa.cavisson.rdt;

import java.util.HashMap;
import java.util.Set;
public class RDTBasedResponse {
  private HashMap<String,String> headers = null;
  private String response = null ;
  private int statusCode = -1 ;
  public RDTBasedResponse(String response,int statusCode){
    this.response = response;
    this.statusCode = statusCode;
    headers = new HashMap<String,String>();
    headers.put("Access-Control-Allow-Origi","*");
    headers.put("Access-Control-Allow-Methods","GET,POST,PUT,OPTIONS,DELETE");
    headers.put("Connection","keep-alive");
    headers.put("Access-Control-Allow-Headers","origin, content-type, accept");
    headers.put("Vary","X-HTTP-Method-Override");
    headers.put("Content-Length",String.valueOf(response.length()));
    headers.put("Content-Type","application/json; charset=utf-8");
    headers.put("X-Powered-By","Express");
  }
  public void setHeader(String headerName , String headerValue){
    headers.put(headerName,headerValue);
  }
  public Set<String> getHeaderNames(){
    return headers.keySet();
  }
  public void getHeader(String headerName){
    return headers.get(headerName);
  }
  public void setResponse(String response){
    this.response = response ;
  }
  public String getResponse(){
    return this.response;
  }
  public void setStatusCode(int statusCode){
    this.statusCode = statusCode;
  }
  public void getStatusCode(){
    return this.statusCode;
  }
}
