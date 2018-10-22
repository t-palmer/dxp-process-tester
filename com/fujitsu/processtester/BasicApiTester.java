package com.fujitsu.processtester;

import java.net.URL;
import com.purplehillsbooks.json.JSONArray;
import com.purplehillsbooks.json.JSONException;
import com.purplehillsbooks.json.JSONObject;
import com.fujitsu.processtester.data.*;

/**
 * Test to make sure all APIs are working fine
 * 
 * @author Anamika Chaudhary
 *
 */
public class BasicApiTester {
    
    public static RunStats testBasicAPIs = new RunStats("Test Basic APIs");    
    
    int workerId;
    private UserConfig userConfig;
    private StressTestConfig config;
    private long startTime = 0;
    private long workingTime = 30000;
    private WorkItemTester currentWorkitem = null;
    
   /* public boolean isWorking() {
        return currentWorkitem!=null;
    }*/
    
    public BasicApiTester(StressTestConfig _config) {
        
        this.config = _config;
        UserConfig workerType = this.config.getUserConfigs().get(0);
        userConfig = workerType;
    }
    
   /* public void step() throws Exception {
        
        //if (!isWorking()) {
            getTenantList();
        }
        else {
            completeWorkWhenReady();
        }
    }*/
    
    public void run(){
    	try {
    		this.getAPIProperties(config, userConfig.getAuthString());
    		
			this.getSystemProperties(config, userConfig.getAuthString());
			Tenant[] tenants = this.getTenantList(config, userConfig.getAuthString());    		
			for(int i=0; i<tenants.length;i++){
				this.getTenantProperties(config, userConfig.getAuthString(),tenants[i]);
				this.getTenantSecurity(config, userConfig.getAuthString(),tenants[i]);
			}
			
			
		} catch (Exception e) {
			JSONException.traceException(e, "BasicApiTester.main");
		}
    }  
    
    public void completeWorkWhenReady() throws Exception {
        long now = System.currentTimeMillis();
        if (now < startTime + workingTime) {
            //not done working yet
            //System.out.println(String.format("Worker %d (%s) has workitem (%d) but not done yet.", workerId,  uc.getName(), currentWorkitem.getId()));
            return;
        }
        
        currentWorkitem.makeRandomChoice(config, userConfig);
        currentWorkitem = null;
        startTime = 0;
    }


    private void getAPIProperties(StressTestConfig config, String authString) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            String urlStr = config.getBaseUrl()+"api/";
            URL url = new URL(urlStr);

            JSONObject jsonData = AA_Processor.sendHttpRequest(url, "GET", authString, null);
            System.out.println("AA Properties");
            System.out.println(jsonData);

            testBasicAPIs.recordPass(startTime);
           
        } catch (Exception e) {
        	testBasicAPIs.recordFail(startTime);
            throw new Exception("Failed to get aa details\n",  e);
        }
    }
    
    private Tenant[] getTenantList(StressTestConfig config, String authString) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            String urlStr = config.getBaseUrl()+"api/Tenants";
            URL url = new URL(urlStr);

            JSONObject jsonWrkItems = AA_Processor.sendHttpRequest(url, "GET", authString, null);
            JSONArray tenantsList = jsonWrkItems.getJSONArray("tenants");
            
            if (tenantsList == null) {
				return new Tenant[0];
			}
            
            Tenant[] tenant = new Tenant[tenantsList.length()];
            for (int i = 0; i < tenantsList.length(); i++) {
            	tenant[i] = new Tenant(tenantsList.getJSONObject(i));
            	System.out.println("Tenant data");
            	System.out.println(tenantsList.get(i));            	
            }

            testBasicAPIs.recordPass(startTime);
            return tenant;
           
        } catch (Exception e) {
            testBasicAPIs.recordFail(startTime);
            throw new Exception("Failed to execute getAllTenants\n",  e);
        }
    }
    
    private void getSystemProperties(StressTestConfig config, String authString) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            String urlStr = config.getBaseUrl()+"api/SystemProperties";
            URL url = new URL(urlStr);

            JSONObject jsonData = AA_Processor.sendHttpRequest(url, "GET", authString, null);
            System.out.println("System properties::::");
            System.out.println(jsonData);

            testBasicAPIs.recordPass(startTime);
           
        } catch (Exception e) {
        	testBasicAPIs.recordFail(startTime);
            throw new Exception("Failed to execute: getting system properties\n",  e);
        }
    }
    
    private void getTenantProperties(StressTestConfig config, String authString, Tenant tenant) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            String urlStr = config.getBaseUrl()+"api/t="+tenant.getName()+"/TenantProperties";
            URL url = new URL(urlStr);

            JSONObject jsonData = AA_Processor.sendHttpRequest(url, "GET", authString, null);
            System.out.println("Tenant properties for tenant: "+tenant.getName());
            System.out.println(jsonData);

            testBasicAPIs.recordPass(startTime);
           
        } catch (Exception e) {
        	testBasicAPIs.recordFail(startTime);
            throw new Exception("Failed to get aa details\n",  e);
        }
    }
    
    private void getTenantSecurity(StressTestConfig config, String authString, Tenant tenant) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            String urlStr = config.getBaseUrl()+"api/t="+tenant.getName()+"/Security";
            URL url = new URL(urlStr);

            JSONObject jsonData = AA_Processor.sendHttpRequest(url, "GET", authString, null);
            System.out.println("Tenant Security properties for tenant: "+tenant.getName());
            System.out.println(jsonData);

            testBasicAPIs.recordPass(startTime);
           
        } catch (Exception e) {
        	testBasicAPIs.recordFail(startTime);
            throw new Exception("Failed to get aa details\n",  e);
        }
    }
    
    public void cleanUp() throws Exception {
        if (currentWorkitem!=null) {
            currentWorkitem.declineIt(config, userConfig);
        }
    }

    public void dumpStatus() {
        //if (isWorking()) {
            int secondsLeft = (int)((startTime + workingTime - System.currentTimeMillis())/1000);
            int totalSeconds = (int)(workingTime/1000);
            System.out.println(String.format("%3d. %s - %s (%s) has %d seconds left of %d", workerId, 
                    userConfig.getName(), currentWorkitem.getName(), currentWorkitem.getId(), secondsLeft,totalSeconds));
        /*}
        else {
            System.out.println(String.format("%3d. %s - idle for %d seconds", 
                    workerId, userConfig.getName(), (System.currentTimeMillis()-idleStart)/1000));
        }*/
    }
    
}
