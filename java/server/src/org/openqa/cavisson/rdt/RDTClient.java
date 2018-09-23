package org.openqa.cavisson.rdt;
import java.util.logging.Logger;
import org.openqa.cavisson.AdbCommandExecutor;
public class RDTClient{
  private static final Logger log = Logger.getLogger(RDTClient.class.getName());
  private AdbCommandExecutor adbCommandExecutor = null ;
  public RDTClient()throws Exception{
    adbCommandExecutor = new AdbCommandExecutor();
  }
  public RDTClient(String deviceId)throws Exception{
    adbCommandExecutor = new AdbCommandExecutor(deviceId);
  }
  public void execute(RDTBasedRequest rdtBasedRequest){
    try{
      log.fine(" desired capabilities "+ rdtBasedRequest.getDesiredCapabilities() + " Extra Capabilities " +rdtBasedRequest.getExtraCapabilities() + " Method " +rdtBasedRequest.getMethod() + "Request Path" +rdtBasedRequest.getRequestPath() + " New SessionRequest " +rdtBasedRequest.getNewSessionrequest());
      log.fine(" Release Version = "+adbCommandExecutor.getReleaseVersion()); 
      log.fine(" SDK Version  = "+adbCommandExecutor.getBuildVersion()); 
      log.fine(" Manufacturer  = "+adbCommandExecutor.getManufacturer()); 
      log.fine(" WindowSize = "+adbCommandExecutor.getWindowSize()); 
      log.fine(" Model = "+adbCommandExecutor.getModel());
      log.fine(" Is Package Available on Device "+adbCommandExecutor.isPackageAvailableOnDevice(rdtBasedRequest.getDesiredCapabilities().get("appPackage").toString())); 
      log.fine(" Starting the package on Device "+adbCommandExecutor.launchApplicationByPackageName(rdtBasedRequest.getDesiredCapabilities().get("appPackage").toString(),rdtBasedRequest.getDesiredCapabilities().get("appActivity").toString())); 
    }catch(Exception e){log.fine(" Exception Caught "+e.getMessage());}
  }

}
