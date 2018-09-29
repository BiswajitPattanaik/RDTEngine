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
      Map<String,Object> desiredCapabilities = rdtBasedRequest.getDesiredCapabilities();
      Map<String,Object> extraCapabilities = rdtBasedRequest.getExtraCapabilities();
      String method = rdtBasedRequest.getMethod();
      String requestPath = rdtBasedRequest.getRequestPath();
      boolean newSessionRequest = rdtBasedRequest.getNewSessionrequest(); 
      log.fine(" desired capabilities "+ desiredCapabilities + " Extra Capabilities " + extraCapabilities  + " Method " + method  + "Request Path" + requestPath  + " New SessionRequest " + newSessionRequest );
      String releaseVersion = adbCommandExecutor.getReleaseVersion();
      String buildVersion = adbCommandExecutor.getBuildVersion();
      String manufacturer = adbCommandExecutor.getManufacturer();
      String model = adbCommandExecutor.getModel();
      String windowSize = adbCommandExecutor.getWindowSize();
      String platform = rdtBasedRequest.getDesiredCapabilities().containsKey("platform")?;
      
      boolean isPackageAvailable = adbCommandExecutor.isPackageAvailableOnDevice(rdtBasedRequest.getDesiredCapabilities().get("appPackage").toString());
      boolean launchApplicationByName = adbCommandExecutor.launchApplicationByPackageName(rdtBasedRequest.getDesiredCapabilities().get("appPackage").toString(),rdtBasedRequest.getDesiredCapabilities().get("appActivity").toString()); 
      log.fine(" Release Version = " + releaseVersion); 
      log.fine(" SDK Version  = " + buildVersion); 
      log.fine(" Manufacturer  = " + manufacturer); 
      log.fine(" WindowSize = " + model); 
      log.fine(" Model = " + windowSize);
      log.fine(" Is Package Available on Device " + isPackageAvailable); 
      log.fine(" Starting the package on Device " + launchApplicationByName);
      Gson gson = new Gson();
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("platform",)      
            
 
    }catch(Exception e){log.fine(" Exception Caught "+e.getMessage());}
  }

}
