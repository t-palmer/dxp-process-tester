package com.fujitsu.processtester;

import java.net.URL;
import java.util.Random;

import com.purplehillsbooks.json.JSONArray;
import com.purplehillsbooks.json.JSONObject;

import com.fujitsu.processtester.data.StressTestConfig;
import com.fujitsu.processtester.data.UserConfig;

public class WorkingUser {
    
    public static RunStats getWorklistStats = new RunStats("Get Worklist");    
    
    int workerId;
    private UserConfig userConfig;
    private StressTestConfig config;
    private long startTime = 0;
    private long idleStart = System.currentTimeMillis();
    private long workingTime = 30000;
    private long lastWorklistCheck = 0;
    private Random _random = new Random(System.currentTimeMillis());
    private WorkItemTester currentWorkitem = null;
    
    public boolean isWorking() {
        return currentWorkitem!=null;
    }
    
    public WorkingUser(StressTestConfig _config, UserConfig _userConfig, int newId) {
        workerId = newId;
        config = _config;
        userConfig = _userConfig;
    }
    
    public void step() throws Exception {
        
        if (!isWorking()) {
            pickUpWork();
        }
        else {
            completeWorkWhenReady();
        }
    }
    
    public void pickUpWork() throws Exception {
        
        long now = System.currentTimeMillis();
        if (now-lastWorklistCheck < config.getWorklistCheckTime()) {
            //silently ignore the request until time
            return;
        }
        
        // list up all work items that the assignee has.
        WorkItemTester[] wis = getAllMyWorkItems(config, userConfig.getAuthString());
        lastWorklistCheck = now;
        if (wis.length == 0) {
            //System.out.println(String.format("No work item for %d (%s).   Still idle.", workerId, uc.getName()));
            return;
        }

        // make choice for a workitem that he/she has
        int idx = (int) Math.floor(_random.nextDouble() * wis.length);

        WorkItemTester selected = wis[idx];
        if (selected.getState()==8) {
            System.out.println("Found a workitem I had already accepted!   Skipping.");
            return;
        }
                
        //set these only if the accept succeeds
        if (selected.acceptIt(config, userConfig)) {
            currentWorkitem = selected;
            startTime = now;
            workingTime = userConfig.getWorkingTime(selected.getName()) * 1000;
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
        idleStart = now;
    }


    private WorkItemTester[] getAllMyWorkItems(StressTestConfig config, String authString) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            String urlStr = config.getAppBaseUrl()+"MyActiveWorkItems";
            URL url = new URL(urlStr);

            JSONObject jsonWrkItems = AA_Processor.sendHttpRequest(url, "GET", authString, null);
            JSONArray wiList = jsonWrkItems.getJSONArray("wiList");
            
            WorkItemTester[] wrkItems = new WorkItemTester[wiList.length()];
            for (int i = 0; i < wiList.length(); i++) {
                wrkItems[i] = new WorkItemTester(wiList.getJSONObject(i));
            }

            getWorklistStats.recordPass(startTime);
            return wrkItems;
        } catch (Exception e) {
            getWorklistStats.recordFail(startTime);
            throw new Exception("Failed to execute AllMyActiveWorkItems\n",  e);
        }
    }
    
    public void cleanUp() throws Exception {
        if (currentWorkitem!=null) {
            currentWorkitem.declineIt(config, userConfig);
        }
    }

    public void dumpStatus() {
        if (isWorking()) {
            int secondsLeft = (int)((startTime + workingTime - System.currentTimeMillis())/1000);
            int totalSeconds = (int)(workingTime/1000);
            System.out.println(String.format("%3d. %s - %s (%s) has %d seconds left of %d", workerId, 
                    userConfig.getName(), currentWorkitem.getName(), currentWorkitem.getId(), secondsLeft,totalSeconds));
        }
        else {
            System.out.println(String.format("%3d. %s - idle for %d seconds", 
                    workerId, userConfig.getName(), (System.currentTimeMillis()-idleStart)/1000));
        }
    }
    
}
