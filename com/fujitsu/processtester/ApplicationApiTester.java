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
public class ApplicationApiTester {
    
    public static RunStats testApplicationAPIs = new RunStats("Test Application APIs");    
    
    int workerId;
    private UserConfig userConfig;
    private StressTestConfig config;
    private long startTime = 0;
    private long workingTime = 30000;
    private WorkItemTester currentWorkitem = null;
    
    
    public ApplicationApiTester(StressTestConfig _config) {
        
        this.config = _config;
        UserConfig workerType = this.config.getUserConfigs().get(0);
        userConfig = workerType;
    }
    
    
    public void run(){
    	try {
			Tenant[] tenants = this.getTenantList(config, userConfig.getAuthString());    		
			for(int i=0; i<tenants.length;i++){
				Application[] applications = this.getTenantApplication(config, userConfig.getAuthString(),tenants[i]);
				
			}
			
			
		} catch (Exception e) {
			JSONException.traceException(e, "ApplicationApiTester.main");
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

            testApplicationAPIs.recordPass(startTime);
            return tenant;
           
        } catch (Exception e) {
            testApplicationAPIs.recordFail(startTime);
            throw new Exception("Failed to execute getAllTenants\n",  e);
        }
    }
    
    
    private Application[] getTenantApplication(StressTestConfig config, String authString, Tenant tenant) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            String urlStr = config.getBaseUrl()+"api/t="+tenant.getName()+"/Applications";
            URL url = new URL(urlStr);

            JSONObject jsonData = AA_Processor.sendHttpRequest(url, "GET", authString, null);
            System.out.println("Tenant applications for tenant: "+tenant.getName());
            System.out.println(jsonData);
            
            JSONArray applicationList = jsonData.getJSONArray("applications");
            
            if (applicationList == null) {
				return new Application[0];
			}
            
            Application[] application = new Application[applicationList.length()];
            /*for (int i = 0; i < applicationList.length(); i++) {
            	application[i] = new Application(applicationList.getJSONObject(i));
            	System.out.println("Application data");
            	System.out.println(applicationList.get(i));            	
            }*/

            testApplicationAPIs.recordPass(startTime);
            return application;
           
        } catch (Exception e) {
        	testApplicationAPIs.recordFail(startTime);
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
