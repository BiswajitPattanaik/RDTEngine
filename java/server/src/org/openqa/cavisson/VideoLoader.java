package org.openqa.cavisson;
import java.io.*;
import java.util.logging.Logger;
public class VideoLoader extends Thread
{
    public String videoName ;
    public String workingDir;
    public String sessionId;
    private static final Logger log = Logger.getLogger(VideoLoader.class.getName());
    public VideoLoader(String videoName,String workingDir , String sessionId)
    {
        super();
        this.videoName = videoName;
        this.workingDir = workingDir;
        this.sessionId = sessionId;
    }
    private boolean videoLoad(String videoName)throws Exception
    {  
        log.fine("pulling video = "+videoName); 
        String lsCmd = "/home/netstorm/Android/Sdk/platform-tools/adb shell ls /sdcard/"+videoName.split("\\.")[0]+"*"+videoName.split("\\.")[1];
        log.fine("ls Cmd = "+lsCmd);
        String videoNameNew = runCommandOutput(lsCmd);
        if(videoNameNew.contains("No such file or directory") || videoNameNew.equals(""))
        {
            return false;
        }
        log.fine("VideoNameNew"+videoNameNew);
        //String pullCmd = "/home/netstorm/Android/Sdk/platform-tools/adb pull /sdcard/"+videoNameNew.split("/")[2]+" /tmp/"+videoName;
        String pullCmd = "/home/netstorm/Android/Sdk/platform-tools/adb pull /sdcard/"+videoNameNew.split("/")[2]+" "+workingDir+"/"+sessionId+"/"+videoName;
        log.fine("adb PULL "+pullCmd);
        String output = runCommandOutput(pullCmd);
        //System.out.println(output);
        return true;
    }
    public void run()
    {
        try{
            if(videoLoad(videoName))
            {
                log.fine("Video load for " +videoName+" completed succesfuly");
            }
            else
            {    
                log.fine("Video load for " +videoName+" completed Unsuccesfuly");  
            }
        }catch(Exception e){e.printStackTrace();}
    }
    public String runCommandOutput(String cmd)throws Exception
    {
        Process p = Runtime.getRuntime().exec(cmd);
        log.fine("The command executed is"+cmd);
        p.waitFor();
        BufferedReader buf = new BufferedReader(new InputStreamReader(
        p.getInputStream()));
        String line = "";
        String output = "";
        while ((line = buf.readLine()) != null) {
            output = output+""+line;
        }
        log.fine("inside command runner i,exit code ="+p.exitValue());
        log.fine("Command output is = "+output);
        return output;
    }

}
