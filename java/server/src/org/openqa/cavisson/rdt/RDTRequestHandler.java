package org.openqa.cavisson.rdt;
import java.util.logging.Logger;
import org.openqa.cavisson.RDTException;

public class RDTRequestHandler {
  private static final Logger log = Logger.getLogger(RDTRequestHandler.class.getName());
  private String internakKey = null;
  private String deviceId = null;
  private boolean newSessionRequest ;
  private RDTClient rdtClient = null;
  public RDTRequestHandler(String deviceId)throws RDTException,Exception{
    rdtClient = new RDTClient(deviceId);
  }
  public RDTRequestHandler()throws RDTException,Exception{
    rdtClient = new RDTClient();
  }  
  public RDTBasedResponse execute(RDTBasedRequest rdtBasedRequest)throws RDTException,Exception{
    String response = rdtClient.execute(rdtBasedRequest);
    RDTBasedResponse rdtBasedResponse = new RDTBasedResponse(response,200);
    return rdtBasedResponse;
  }
  
}
