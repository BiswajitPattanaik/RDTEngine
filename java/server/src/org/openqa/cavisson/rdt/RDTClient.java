package org.openqa.cavisson.rdt;
import java.util.logging.Logger;
public class RDTClient{
  private static final Logger log = Logger.getLogger(RDTClient.class.getName());
  public void execute(RDTBasedRequest rdtBasedRequest){
     log.fine(" desired capabilities "+ rdtBasedRequest.getDesiredCapabilities() + " Extra Capabilities " +rdtBasedRequest.getExtraCapabilities() + " Method " +rdtBasedRequest.getMethod() + "Request Path" +rdtBasedRequest.getRequestPath() + " New SessionRequest " +rdtBasedRequest.getNewSessionrequest());
  }

}
