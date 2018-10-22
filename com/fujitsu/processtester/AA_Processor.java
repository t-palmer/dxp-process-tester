package com.fujitsu.processtester;

import java.io.*;
import java.net.*;
import java.util.*;

import com.purplehillsbooks.json.*;

import com.fujitsu.processtester.data.*;

/**
 * Calls Agile Adapter REST API
 * 
 * @author sawadary
 *
 */
public class AA_Processor {
    
	/**
	 * Get all process instances
	 * 
	 * @param baseUrl
	 * @param authString
	 * @return
	 */
	public static ProcessItem[] getAllProcessInstances(StressTestConfig config, int max) throws Exception {

		List<ProcessItem> processes = null;
		try {
		    JSONObject requestParams = new JSONObject();
		    requestParams.put("batchSize", max+1);
            requestParams.put("estimateCount", true);
		    
			String urlStr = config.getAppBaseUrl()+"/AllActiveProcesses";
			URL url = new URL(urlStr);
			JSONObject processItems = sendHttpRequest(url, "POST", config.getUserRandom().getAuthString(), requestParams);
			JSONArray piList = processItems.getJSONArray("piList");

			if (piList == null) {
				return new ProcessItem[0];
			}

			processes = new LinkedList<ProcessItem>();

			for (int i = 0; i < piList.length(); i++) {
				ProcessItem pi = createProcessItem(piList.getJSONObject(i));
				processes.add(pi);
			}

		} catch (Exception e) {
			throw new Exception("Failed to get the process list: ",e);
		}

		return processes.toArray(new ProcessItem[0]);
	}


	/**
	 * Create an instance from JSON
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 */
	private static ProcessItem createProcessItem(JSONObject json) throws Exception {
		ProcessItem pi = new ProcessItem(json);
		return pi;
	}




	/**
	 * Send http request
	 * 
	 * @param url
	 * @param method
	 *            "GET", "POST", etc
	 * @param authString
	 *            Authentication string
	 * @param content
	 *            Content when using POST
	 * @return JSON object.
	 * @throws Exception
	 */
	public static JSONObject sendHttpRequest(URL url, String method, String authString, JSONObject content)
			throws Exception {
		HttpURLConnection httpCon = null;
		httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setRequestMethod(method);
		httpCon.setDoOutput(true);
		httpCon.setDoInput(true);
		httpCon.setRequestProperty("AgileAuth", authString);
		httpCon.connect();

		if (!method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("DELETE")) {
			OutputStream os = httpCon.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			content.write(osw, 2, 0);
			osw.flush();
			osw.close();

			os.close();
		}

		int retCode = httpCon.getResponseCode();
        JSONObject resp = null;
        InputStream is = null;
        if (retCode == 200) {
            is = httpCon.getInputStream();
        }
        else {
            is = httpCon.getErrorStream();
        }
		try {
		    
    		JSONTokener jt = new JSONTokener(is);
    		resp = new JSONObject(jt);
		}
		catch (Exception er) {
		    if (retCode == 200) {
		        throw new Exception("HTTP call failed (code="+retCode+") (unknown error body) for "+url, er);
		    }
		    else {
		        throw new Exception("HTTP call failed (code="+retCode+") (unknown error body) for "+url);
		    }
		}
		finally {
		    is.close();
    		httpCon.disconnect();
		}
		if (retCode==200) {
		    return resp;
		}
	    if (resp.has("error")) {
	        Exception remoteException = JSONException.convertJSONToException(resp);
	        throw new Exception("HTTP call failed (code="+retCode+") for "+url, remoteException);
	    }
	    throw new Exception("HTTP call failed (code="+retCode+") for "+url);
	}
	
}
