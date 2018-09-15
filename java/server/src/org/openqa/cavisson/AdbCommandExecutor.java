package org.openqa.cavisson;
import java.util.logging.Logger;
import java.io.*;

public class AdbCommandExecutor{
  public final String  ABD_SDK_RELATIVE_PATH = "platform-tools/adb";
  private String adbExecutorPath = null;
  private String adbExecutorRelativePath = ABD_SDK_RELATIVE_PATH;
  private String deviceId = null; 
  
  //Constructor for "AdbCommandExecutor" Class
  private static final Logger log = Logger.getLogger(AdbCommandExecutor.class.getName());

  public AdbCommandExecutor()throws Exception{
    this.adbExecutorPath = getAdbExecutorPath();
    this.deviceId = getDeviceId();
  }
  

  //Constructor for "AdbCommandExecutor" Class
  public AdbCommandExecutor(String deviceId)throws Exception{
    this.adbExecutorPath = getAdbExecutorPath();
    this.deviceId = deviceId;
  } 

  //Run Command and get Command Output
  public String runCommandOutput(String cmd)throws Exception{
    Process p = Runtime.getRuntime().exec(adbExecutorPath + " -s " + deviceId + " " + cmd);
    p.waitFor();
    log.fine("The command executed is"+cmd);
    BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line = "";
    String output = "";
    while ((line = buf.readLine()) != null) {
      output = output+""+line;
    }
    log.fine("Command output is = "+output);
    return output;
  }
 
  //Run Command and get Command Exit Status
  public int runCommandExitValue(String cmd)throws Exception{
    Process p = Runtime.getRuntime().exec(adbExecutorPath + " -s " + deviceId + " " + cmd);
    p.waitFor();
    log.fine("The command executed is"+cmd);
    log.fine("inside command runner i,exit code ="+p.exitValue());
    return p.exitValue();
  } 

  public boolean ping()throws Exception{
    Process p = Runtime.getRuntime().exec(adbExecutorPath + " -s " + deviceId + " shell echo ping" );
    p.waitFor();
    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream())); 
    String line = null;
    String pingoutput = (line = input.readLine()) != null ? line : null;
    if (pingoutput.equals("ping") && p.exitValue() == 0 ){
      return true;
    }
    else{
      return false;
    }
      
  }

  //GetExecutorPath for Adb 
  private String getAdbExecutorPath()throws RDTException,Exception{
    String AndroidHome = System.getenv("ANDROID_HOME");
    if (AndroidHome == null){
      throw new RDTException(" Android Home is not present hence exiting  ");
    }
    adbExecutorPath = AndroidHome + "/" + adbExecutorRelativePath ;
    return adbExecutorPath;
  }

  //Get Default deviceId inCase DeviceId is not Specified
  private String getDeviceId()throws RDTException,Exception{
    Process p = Runtime.getRuntime().exec(adbExecutorPath + " devices"); 
    p.waitFor();
    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream())); 
    String line = null;
    String deviceInfo = (line = input.readLine()) != null ? line : null;
    String deviceId = null;
    if(deviceInfo != null){
      throw new RDTException("No Devices Seems to be connected Hence Exiting ");
    }   
    deviceId = deviceInfo.split("\t",-1)[0];
    deviceInfo = (line = input.readLine()) != null ? line : null; 
    if(deviceInfo != null){
      throw new RDTException("More Than once Devices Seems to be connected , Please Specify a deviceId");
    }   
    return deviceId; 
  } 

}
