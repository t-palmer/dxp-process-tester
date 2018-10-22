package com.fujitsu.processtester;

import java.net.URL;

import com.fujitsu.processtester.data.StressTestConfig;
import com.purplehillsbooks.json.JSONException;
import com.purplehillsbooks.json.JSONObject;

/**
 * This is a very simple class, it is a thread that simply changes the application variable values as quickly as 
 * it can in order to see if there are any conflicts produced which this is run multiple times
 * at once.
 */
public class AppVarThread extends Thread {
    
    StressTestConfig config;
    boolean running = true;
    String currentVal = "0";
    
    public AppVarThread(StressTestConfig _config) {
        config = _config;
    }
    
    public void die() {
        running = false;
    }

    public void run() {
        int count = 0;
        System.out.println("Running Thread to: "+config.getAppBaseUrl()+"/AppVars");
        while (running) {
            if (++count % 100 == 0) {
                System.out.println("Completed "+count+" updates, value is "+currentVal);
            }
            try {
                //we are in a fast loop doing this as fast as possible 99 out of 100 times
                incrementAppVars();
            
            }
            catch (Exception e) {
                JSONException.traceException(System.out, e, "FAILURE on try #"+count);
            }
        }
    }   
    
    public void incrementAppVars() throws Exception {

        try {
            String urlStr = config.getAppBaseUrl()+"/AppVars";
            URL url = new URL(urlStr);
            JSONObject appInfo = AA_Processor.sendHttpRequest(url, "GET", config.getAdminAuth(), null);
            
            JSONObject newVersion = new JSONObject();
            //now update them
            incrementOneValue(appInfo, newVersion, "testVal1");
            incrementOneValue(appInfo, newVersion, "testVal2");
            incrementOneValue(appInfo, newVersion, "testVal3");
            
            currentVal = newVersion.getString("testVal1");
            
            AA_Processor.sendHttpRequest(url, "POST", config.getAdminAuth(), newVersion);

        } catch (Exception e) {
            throw new Exception("Failed to increment the application variable values: ",e);
        }
    }
    
    public void incrementOneValue(JSONObject input, JSONObject output, String name) throws Exception {
        if (input.has(name)) {
            long val = safeConvertLong(input.getString(name)) + 1;
            output.put(name, Long.toString(val));
        }
        else {
            System.out.println("WARNING: application variable ("+name+") did not exist.");
            output.put(name, "1");
        }
    }
    
    public static long safeConvertLong(String val) {
        if (val == null) {
            return 0;
        }
        long res = 0;
        int last = val.length();
        for (int i = 0; i < last; i++) {
            char ch = val.charAt(i);
            if (ch >= '0' && ch <= '9') {
                res = res * 10 + ch - '0';
            }
        }
        return res;
    }
    
}
