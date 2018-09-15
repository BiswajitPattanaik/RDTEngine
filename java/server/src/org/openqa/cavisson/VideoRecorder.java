package org.openqa.cavisson;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.*;
import org.openqa.cavisson.VideoLoader;
import java.util.logging.Logger;
import java.util.Arrays;
public class VideoRecorder{
    private int processId ;
    private boolean recordingFlag = false ;
    private int initial_count = 0;
    private String sessionId = null;
    private String workingDir =  null;
    private String sessionVideoPath = null;
    private String sessionDirPath = null;
    private String deviceId = null;
    private static final Logger log = Logger.getLogger(VideoRecorder.class.getName());

    public static void main(String[] s)throws Exception
    {
        /*VideoRecorder recorder = new VideoRecorder();
        recorder.startRecording();
        Thread.sleep(150000);
        recorder.stopRecording();*/
    }
    public VideoRecorder(String workingDir , String sessionId,String deviceId){
        this.workingDir = workingDir;
        this.sessionId = sessionId ;
        this.deviceId = deviceId;
        sessionVideoPath = workingDir+"/"+sessionId+"/SessionVideo.mp4";
        sessionDirPath = workingDir+"/"+sessionId;
    }
    public void startRecording()throws Exception
    {  
        String cmd; 
        if(recordingFlag)
        {
            log.fine("recording is already started");
        }
        else
        {
            cmd="/bin/bash /home/netstorm/work/automation/start_recording";
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            String output = "";
            while ((line = buf.readLine()) != null) {
                output = output+""+line;
            }
            log.fine("Start recording output = "+output);
            processId = Integer.parseInt(output);
            recordingFlag = true ;
        } 
    }
    public void stopRecording(HttpServletResponse response)throws Exception
    {   
        String cmd;
        cmd =  "kill -9 "+processId;    
        if(recordingFlag)
        {    
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            Thread.sleep(2000);
            int counter = 1;
            initial_count = Thread.activeCount();
            while(counter <=3)
            {
                new Thread(new VideoLoader("test"+counter+".mp4",workingDir,sessionId,deviceId)).start();
                counter++;
                log.fine("Current Thread count is"+Thread.activeCount());
            }
            while(Thread.activeCount()>initial_count)
            {
                Thread.sleep(2000);
		log.fine("[INFO] Threads are running to copy clips from device to machine");
            }
            counter = 1; 
            File f;
            PrintWriter out = new PrintWriter(new FileOutputStream(new File(sessionDirPath+"/file.txt"),false));
            while(counter <= 3 )
            {
                f=new File(sessionDirPath+"/test"+counter+".mp4");
                if(f.exists())
                {
                   log.fine("file '"+sessionDirPath+"/test"+counter+".mp4'");
                   out.println("file '"+sessionDirPath+"/test"+counter+".mp4'");
                }
                counter++;      
            }
            out.close();
            p = Runtime.getRuntime().exec("/home/cavisson/work/ffmpeg-2.8.5/ffmpeg -f concat -safe 0 -i "+sessionDirPath+"/file.txt"+" -c copy -fflags +genpts "+sessionVideoPath);
            p.waitFor();
            byte[] body = getBytesFromFile(new File(sessionVideoPath));
            //log.fine(" Byte Array is given as ="+Arrays.toString(body));
            ByteArrayOutputStream baos = new ByteArrayOutputStream(body.length);
            baos.write(body, 0, body.length);
            response.setContentType("video/mp4");
            response.setContentLength(body.length);
            response.setHeader("Content-Disposition", "inline; filename="+ sessionVideoPath +";");
 
            ServletOutputStream servletOutputStream = response.getOutputStream();
            baos.writeTo(servletOutputStream);
            baos.close();
            servletOutputStream.flush();
            servletOutputStream.close();
        } 
        else
        {
            log.fine("Recording is not in action");
        }   

    }

public byte[] getBytesFromFile(File file) throws IOException {        
        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
            throw new IOException("File is too large!");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        InputStream is = new FileInputStream(file);
        try {
            while (offset < bytes.length
                   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        } finally {
            is.close();
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
        return bytes;
    }

}
