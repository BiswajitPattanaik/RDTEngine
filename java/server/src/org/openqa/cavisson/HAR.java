package org.openqa.cavisson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.Set;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collections;
import java.util.logging.Logger;

class ResourceTiming 
{
	double connectEnd;
	double connectStart;
	double dnsEnd;
	double dnsStart;
	double proxyEnd;
	double proxyStart;
	double receiveHeadersEnd;
	double requestTime;
	double sendEnd;
	double sendStart;
	double sslEnd;
	double sslStart;
	public double getConnectEnd() {
		return connectEnd;
	}
	public void setConnectEnd(double connectEnd) {
		this.connectEnd = connectEnd;
	}
	public double getConnectStart() {
		return connectStart;
	}
	public void setConnectStart(double connectStart) {
		this.connectStart = connectStart;
	}
	public double getDnsEnd() {
		return dnsEnd;
	}
	public void setDnsEnd(double dnsEnd) {
		this.dnsEnd = dnsEnd;
	}
	public double getDnsStart() {
		return dnsStart;
	}
	public void setDnsStart(double dnsStart) {
		this.dnsStart = dnsStart;
	}
	public double getProxyEnd() {
		return proxyEnd;
	}
	public void setProxyEnd(double proxyEnd) {
		this.proxyEnd = proxyEnd;
	}
	public double getProxyStart() {
		return proxyStart;
	}
	public void setProxyStart(double proxyStart) {
		this.proxyStart = proxyStart;
	}
	public double getReceiveHeadersEnd() {
		return receiveHeadersEnd;
	}
	public void setReceiveHeadersEnd(double receiveHeadersEnd) {
		this.receiveHeadersEnd = receiveHeadersEnd;
	}
	public double getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(double requestTime) {
		this.requestTime = requestTime;
	}
	public double getSendEnd() {
		return sendEnd;
	}
	public void setSendEnd(double sendEnd) {
		this.sendEnd = sendEnd;
	}
	public double getSendStart() {
		return sendStart;
	}
	public void setSendStart(double sendStart) {
		this.sendStart = sendStart;
	}
	public double getSslEnd() {
		return sslEnd;
	}
	public void setSslEnd(double sslEnd) {
		this.sslEnd = sslEnd;
	}
	public double getSslStart() {
		return sslStart;
	}
	public void setSslStart(double sslStart) {
		this.sslStart = sslStart;
	}
	
	
}
class Response 
{
	String connectionId;
	boolean connectionReused;
	boolean fromDiskCache;
	HashMap<String, String> headers;
	String headersText;
	String mimeType;
	HashMap<String, String> requestHeaders;
	String requestHeadersText;
	int status;
	String statusText;
	ResourceTiming timing;
	String url;
	String remoteIPAddress;
	String remotePort;
	public String getConnectionId() {
		return connectionId;
	}
	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}
	public boolean isConnectionReused() {
		return connectionReused;
	}
	public void setConnectionReused(boolean connectionReused) {
		this.connectionReused = connectionReused;
	}
	public boolean isFromDiskCache() {
		return fromDiskCache;
	}
	public void setFromDiskCache(boolean fromDiskCache) {
		this.fromDiskCache = fromDiskCache;
	}
	
	public String getHeadersText() {
		return headersText;
	}
	public void setHeadersText(String headersText) {
		this.headersText = headersText;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	public HashMap<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
	public HashMap<String, String> getRequestHeaders() {
		return requestHeaders;
	}
	public void setRequestHeaders(HashMap<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}
	public String getRequestHeadersText() {
		return requestHeadersText;
	}
	public void setRequestHeadersText(String requestHeadersText) {
		this.requestHeadersText = requestHeadersText;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getStatusText() {
		return statusText;
	}
	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}
	public ResourceTiming getTiming() {
		return timing;
	}
	public void setTiming(ResourceTiming timing) {
		this.timing = timing;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRemoteIPAddress() {
		return remoteIPAddress;
	}
	public void setRemoteIPAddress(String remoteIPAddress) {
		this.remoteIPAddress = remoteIPAddress;
	}
	public String getRemotePort() {
		return remotePort;
	}
	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
	}
}
class Header
{
	String name;
	String value;
	public String toString() {
		return "{\""+name+"\":\""+value+"\"}";
	}
	public Header(String name, String value)
	{
	   this.name = name;
	   this.value = value;
	}
}

class Request
{
        HashMap<String, String> headers;	
        String method;
        String postData;
        String url;
	
	public HashMap<String, String> getHeaders() {
	        return headers;
        }
        public void setHeaders(HashMap<String, String> headers) {
	        this.headers = headers;
        }
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getPostData() {
		return postData;
	}
	public void setPostData(String postData) {
		this.postData = postData;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}	  
}

class pageTiming 
{
	int onContentLoad;
        int onLoad;
        int _cav_startRender;
        int _cav_endRender;
        int _tti;
    
        public pageTiming() 
        {
    	  onContentLoad = -1;
    	  onLoad = -1;
    	  _cav_endRender = -1;
          _cav_startRender = -1;
    	  _tti = -1;
	}
}

class harPage
{
	String startedDateTime; //TODO: check if we should take this in string format?
	String id;
	String title;
	pageTiming pageTimings;
	

	public harPage(String startedDateTime, String title,String id) {
		this.startedDateTime = startedDateTime;
		this.title = title;
		this.id= id;
		this.pageTimings = new pageTiming();
	}

      public harPage()
      {
      }	
}

class harCookie 
{
	String name;
	String value;
	//Time format string
	String expires;
	boolean httpOnly;
	boolean secure;
}

class harQueryString 
{
	String name;
	String value;
	public harQueryString(String name, String value) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.value = value;
	}
}
class harRequest
{
	String method;
	String url;
	String httpVersion;
	Header[] headers;
	PostData postData;
        harCookie[] cookies;
        queryString[] queryString;
        int headersSize;
        int bodySize;
}

class harContent 
{
	int size;
	String mimeType;
	int compression;
	String text;
}

class harCache 
{
  	
}

class harResponse 
{
	int status;
	String statusText;
	String httpVersion;
	Header[] headers;
        harCookie[] cookies;
	String redirectURL;
	int headersSize;
	int bodySize;
	harContent content;
}

class harEntry 
{
	String pageref;
	String startedDateTime;
	double time; //duration
        harRequest request;
        harResponse response;
        harCache cache;
        harTiming timings;
        String _cav_cache_provider;
        String _cav_cache_state;
        String serverIPAddress;
        String connection;
        long _startedTimeInMillis;
}

class harTiming 
{
	double blocked;
	double dns;
	double connect;
	double send;
        double wait;
        double receive;
        double ssl;
}

class creator 
{
	String name;
	String version;
	public creator(String lname, String lversion) {
		// TODO Auto-generated constructor stub
		name = lname;
		version = lversion;
	}
}

class harLog
{
	public String version;
	public creator creator;
    public harBrowser browser;
	public harPage[] pages;
	public harEntry[] entries;
}


class queryString
{
	String name;
	String value;
	public queryString() {
		
	}
	public String toString() {
		return "{\""+name+"\":\""+value+"\"}";
	}
	public queryString(String name,String value)
	{
		this.name = name;
		this.value = value;
	}
	
}

class harBrowser
{
   String name;
   String version;
   harBrowser(String name, String version)
   {
      this.name = name;
      this.version = version;
   }
}
class postParams{
	String name ;
	String value;
	public postParams(String name,String value) {
		this.name=name;
		this.value=value;
	}
}

class PostData{
	String mimeType;
	postParams[] params;
        String text;
	public PostData() {
		
	}
	public PostData(String mimeType,postParams[] params) {
		this.mimeType=mimeType;
		this.params=params;
	}
        public PostData(String mimeType , String text){
	        this.mimeType = mimeType;
                this.text = text ;
	}
}
public class HAR {
	//private String harStr;
    private static final Logger LOG = Logger.getLogger(HAR.class.getName());
    public harLog log;
    public HAR() 
    {
	log = new harLog();
    }
    public HAR(String harStr) throws ParseException {
    log = new harLog();
    //this.harStr=harStr;
    fillHarLog(harStr);
    }
    public String getHarMessage() {
    	JsonParser json = new JsonParser();
    	JsonObject js;
		js = (JsonObject)json.parse(toString());
		js.addProperty("message", calculateRbuStats(0));
		return js.toString();
    }
    
    public void fillHarLog(String harString) throws ParseException {
        this.log.version= "1.2";
        this.log.creator= new creator("CavissonHarBuilder", "1.2");
        this.log.browser= new harBrowser("Android Http Url","1.0");
        this.log.pages = new harPage[1];
        Gson gson = new Gson(); 
        JsonObject js;
		JsonParser json = new JsonParser();
		js = (JsonObject)json.parse(harString);
		JsonObject logs = js.getAsJsonObject("log");
		//System.out.println(js.toString());
		JsonObject page = (JsonObject)logs.getAsJsonArray("pages").get(0);
		//System.out.println(page.toString());
		log.pages[0] = new harPage(page.get("startedDateTime").getAsString(),page.get("title").getAsString(),page.get("id").getAsString());
		JsonArray msg = logs.getAsJsonArray("entries");
		ArrayList<harEntry> tempEntries = new ArrayList<harEntry>();
		//System.out.println(msg);
		for (JsonElement obj1 : msg) 
		{
			JsonObject request=((JsonObject)obj1).getAsJsonObject("request");
			JsonObject response=((JsonObject)obj1).getAsJsonObject("response");
			JsonObject cache=((JsonObject)obj1).getAsJsonObject("cache");
			JsonObject timings=((JsonObject)obj1).getAsJsonObject("timings");
			//System.out.println(request.get("method").toString());
			if(!request.get("method").toString().equals("\"CONNECT\"")) {
				JsonObject tmp = (JsonObject)obj1;
				harEntry he = new harEntry();
		        harRequest hreq = new harRequest();
		        harResponse hres = new harResponse();
		        //System.out.println(obj1.toString());
		        he.pageref=log.pages[0].id;
		        he.startedDateTime=tmp.get("startedDateTime").toString().replaceAll("\"", "");
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSX");
		        Date date = sdf.parse(he.startedDateTime);
		        he._startedTimeInMillis=date.getTime();
		        hreq.httpVersion = "HTTP/1.1";
		        hreq.url=request.get("url").toString().replaceAll("\"", "");
		        hreq.method=request.get("method").toString().replaceAll("\"", "");
		        Header[] reqHeader;
		    	Header[] resHeader;
		    	Map<String, String> tmp1 = new HashMap<String,String>();
		    	JsonArray headerJsonArray = request.getAsJsonArray("headers");
		    	hreq.headers=gson.fromJson(headerJsonArray.toString(), Header[].class);
		    	hreq.headersSize=request.get("headersSize").getAsInt();
		    	JsonArray querryStringArray = request.getAsJsonArray("queryString");
		    	//System.out.println(querryStringArray.toString());
		    	hreq.queryString=gson.fromJson(querryStringArray.toString(), queryString[].class);
		    	JsonArray cookiesArray = request.getAsJsonArray("cookies");
		    	hreq.cookies = gson.fromJson(cookiesArray.toString(), harCookie[].class);
		    	if (request.get("method").getAsString().equals("POST") && request.has("postData")) {
		    		JsonObject postDataJson = request.getAsJsonObject("postData");
		    		//System.out.println(postDataJson.toString());
		    		PostData postData= new PostData();
		    		postData.mimeType=postDataJson.get("mimeType").getAsString();
                                LOG.fine(postDataJson.toString());
                                if (postDataJson.has("params")){
                                  LOG.fine(postDataJson.getAsJsonArray("params").toString());
		    		  postData.params = gson.fromJson(postDataJson.getAsJsonArray("params").toString(),postParams[].class);
                                }
                                if (postDataJson.has("text")){
				  LOG.fine(postDataJson.get("text").getAsString());
                                  postData.text = postDataJson.get("text").getAsString();
				}
		    		hreq.postData=postData;
		    	}
		    	hreq.bodySize=request.get("bodySize").getAsInt();
		    	he.request=hreq;
		        hres.status=response.get("status").getAsInt();
		        hres.statusText=response.get("statusText").getAsString();
		        hres.httpVersion="HTTP/1.1";
		        headerJsonArray = response.getAsJsonArray("headers");
		        hres.headers=gson.fromJson(headerJsonArray.toString(), Header[].class);
		        hres.headersSize=response.get("headersSize").getAsInt();
		        cookiesArray = response.getAsJsonArray("cookies");
		        hres.cookies=gson.fromJson(cookiesArray.toString(), harCookie[].class);
		        hres.redirectURL=response.get("redirectURL").getAsString();
		        harContent hC = gson.fromJson(response.getAsJsonObject("content").toString(), harContent.class);
                        JsonObject harContentJson = response.getAsJsonObject("content");
                        if (harContentJson.has("mimeType")){
			    String contentString = harContentJson.get("mimeType").getAsString();
                            hC.mimeType = contentString.substring(0,contentString.indexOf(';') == -1 ? contentString.length() : contentString.indexOf(';'));
			    LOG.fine("Mime type = "+hC.mimeType);
			}
		        hres.content=hC;
		        he.response=hres;
		        he.cache=gson.fromJson(cache.toString(), harCache.class);
		        he.timings=gson.fromJson(timings.toString(),harTiming.class);
		        if (((JsonObject)obj1).has("serverIPAddress")) {
		        	he.serverIPAddress=((JsonObject)obj1).get("serverIPAddress").getAsString();
		        }
		        he.time=((JsonObject)obj1).get("time").getAsDouble();
		        he._cav_cache_provider="Akamai";
		        tempEntries.add(he);    	
			}
		}
		this.log.entries=new harEntry[tempEntries.size()];
		for(int a = 0; a < tempEntries.size(); a++)
	    	  log.entries[a] = tempEntries.get(a);
		
		
	}
    
    public String toString()
    {
	Gson gson = new GsonBuilder().create(); // for beautifying the json.
	return gson.toJson(this, HAR.class);
    }
     // converts har object into  json object with proper indentation.	
     public String toPrettyString()
     {
	Gson gson = new GsonBuilder().setPrettyPrinting().create(); // for beautifying the json.
	return gson.toJson(this, HAR.class);
     }

     // calculating all the stats for rbu gdf.
     public String calculateRbuStats(int mode)
     {
             
             String message = "";
             long domLoadTime = this.log.pages[0].pageTimings.onContentLoad;
             long onloadTime = this.log.pages[0].pageTimings.onLoad;
             long overallTime = 0;
             int timeToInteract = -1;
             int startRenderTime = -1;
             int visuallyCompleteTime = -1;
             int byteRecieved = 0;
             int byteSend = 0;
             int jsSize = 0;
             int cssSize = 0;
             int imgSize = 0;
             int htmlSize = 0;
             int pageAvailability = 0;
             if(this.log.entries != null)
             {
               for(int i = 0; i < this.log.entries.length; i++)
               {
            	// skipping to next in case of 301, 302, 307, 401, 407 status.
                if(Integer.valueOf(this.log.entries[i].response.status) == 301 ||
                   Integer.valueOf(this.log.entries[i].response.status) == 302 ||
                   Integer.valueOf(this.log.entries[i].response.status) == 307 ||
                   Integer.valueOf(this.log.entries[i].response.status) == 401 ||
                   Integer.valueOf(this.log.entries[i].response.status) == 407)
                	continue;
                else
                {
                	if(Integer.valueOf(this.log.entries[i].response.status) == 200 ||
                       Integer.valueOf(this.log.entries[i].response.status) == 304)
                	   pageAvailability = 1;
                	break;
                }
               }
             }
             int backendResponse = 0;
             long minStartTime = 0;
             long maxEndTime = 0;
             int httpRequests = 0;
             int httpRequestsBrowserCache = 0;
             int akamaiOffload = 0;
            
             if(this.log.entries != null)
             {
               for(int i = 0; i < this.log.entries.length; i++)
               {
                 
                 if(i == 0)
                 {
                         minStartTime = this.log.entries[i]._startedTimeInMillis;
                         maxEndTime = this.log.entries[i]._startedTimeInMillis + (long) this.log.entries[i].time;

                         // backendresponse is calculated only using first entry.
                         backendResponse += (this.log.entries[i].timings.dns > 0) ? this.log.entries[i].timings.dns : 0 +
                                    ((this.log.entries[i].timings.connect > 0)? this.log.entries[i].timings.connect : 0) +
                             ((this.log.entries[i].timings.wait > 0)?this.log.entries[i].timings.wait : 0 )+
                             ((this.log.entries[i].timings.receive > 0)? this.log.entries[i].timings.receive : 0);
                 }
                 else
                 {
                         //calculating min start time and max end time out of all the entries for overall time.
                         minStartTime = minStartTime > this.log.entries[i]._startedTimeInMillis ? this.log.entries[i]._startedTimeInMillis :minStartTime;
                         long tmp = this.log.entries[i]._startedTimeInMillis + (long) this.log.entries[i].time;
                         maxEndTime = maxEndTime < tmp ? tmp : maxEndTime;
                 }
                 
                 // in case of har from resource timing following information are not available so skipping.
                 if(mode == 1) 
                 { 
                   httpRequests++; //counting for both browser / non-browser provider in case of RT.
                   continue; 
            	 }

            	 if(this.log.entries[i]._cav_cache_provider.equals("Browser")) // count for browser as provider
            		 httpRequestsBrowserCache++;
            	 else
            	     httpRequests++; // count for non browser provider
                 if(this.log.entries[i]._cav_cache_provider.equals("Akamai")) // count for akamai as provider
                	 akamaiOffload++;
            	 
            	 if(this.log.entries[i].request.bodySize > 0)
            		 byteSend += this.log.entries[i].request.bodySize;
            	 if(this.log.entries[i].request.headersSize > 0)
            		 byteSend += this.log.entries[i].request.headersSize;
            	 
            	 if(this.log.entries[i].response.bodySize > 0)
            		 byteRecieved += this.log.entries[i].response.bodySize;
            	 if(this.log.entries[i].response.headersSize > 0)
            		 byteRecieved += this.log.entries[i].response.headersSize;
            	 
            	 
            	 if(this.log.entries[i].response.content.mimeType.toLowerCase().contains("javascript"))
            		jsSize +=  this.log.entries[i].response.bodySize;
            	 if(this.log.entries[i].response.content.mimeType.toLowerCase().contains("css"))
            		cssSize +=  this.log.entries[i].response.bodySize;
            	 if(this.log.entries[i].response.content.mimeType.toLowerCase().contains("image"))
            		imgSize += this.log.entries[i].response.bodySize;
            	 if(this.log.entries[i].response.content.mimeType.toLowerCase().contains("html"))
             		htmlSize += this.log.entries[i].response.bodySize;
            	 
            	
               }
             }
             
             try
             { 
               // handling exception in case of 0 requests or akamaiOffload .
               akamaiOffload = (akamaiOffload/httpRequests) * 100;
             }
             catch(Exception e)
             {
            	 akamaiOffload = 0;
             }
             overallTime = maxEndTime - minStartTime;
             
             int pageWeight = jsSize + cssSize + imgSize + htmlSize;
             int domElements = 0;
             int pageSpeedMetrics = -1;
             int sessionCompleted = 0;
             int sessionSuccess = 0;
             
             // creating message with stats '|' separated.
             message   +=        domLoadTime + "|" 
            		       + onloadTime + "|"
            		       + overallTime + "|"
            		       + timeToInteract + "|"
            		       + startRenderTime + "|"
            		       + visuallyCompleteTime + "|"
            		       + httpRequests + "|"
            		       + httpRequestsBrowserCache + "|"
            		       + byteRecieved + "|"
            		       + byteSend + "|"
            		       + pageWeight + "|"
            		       + jsSize + "|"
            		       + cssSize + "|"
            		       + imgSize + "|"
            		       + domElements + "|"
            		       + pageSpeedMetrics + "|"
            		       + akamaiOffload + "|"
            		       + backendResponse + "|"
            		       + pageAvailability + "|"
            		       + sessionCompleted + "|"
            		       + sessionSuccess;

             try // appending started time 
             {
               message += "&" + this.log.entries[0]._startedTimeInMillis;
             }
             catch(Exception e) // if no entries then appending 0.
             {
            	 message += "&0";
             }
             return message;      	
     }

     // method to fill har object.
    
}



