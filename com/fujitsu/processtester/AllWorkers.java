package com.fujitsu.processtester;

import java.util.ArrayList;
import java.util.TimerTask;

import com.purplehillsbooks.json.JSONException;
import com.fujitsu.processtester.data.*;

/**
 * Task that make a choice on work item.
 * 
 * @author sawadary
 *
 */
public class AllWorkers extends TimerTask {
    
	@Override
	public void run() {
		try {
			doTask();
		} catch (Exception ex) {
			JSONException.traceException(ex, "Failure while processing all workers.");
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Members
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private ArrayList<WorkingUser> workers = new ArrayList<WorkingUser>();


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Methods
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Constructor
	 * 
	 * @param config
	 */
	public AllWorkers(StressTestConfig config) {
	    int totalCount = 1;
	    for (UserConfig workerType : config.getUserConfigs()) {
            for (int x=workerType.getDuplicateWorkers(); x>0; x--) {
                workers.add(new WorkingUser(config, workerType, totalCount++));
            }       
	    }
	}

	/**
	 * List up work items and make it complete one work item.
	 * 
	 * @throws Exception
	 */
	private void doTask() throws Exception {
	    int isWorking = 0;
	    int notWorking = 0;
	    for (WorkingUser wu : workers) {
	        try {
	            wu.step();
	        }
	        catch (Exception e) {
	            JSONException.traceException(e, "Failure processing worker: "+wu.workerId);
	        }
	        if (wu.isWorking()) {
	            isWorking++;
	        }
	        else {
	            notWorking++;
	        }
	    }
        System.out.println("--- Working="+isWorking+", Not Working="+notWorking);

	}
	
	public void dumpWorkerStatus() {
        System.out.println("-------");
        for (WorkingUser wu : workers) {
            wu.dumpStatus();
        }
        System.out.println("-------");
	}
	

	public void cleanUp() throws Exception {
	    for (WorkingUser wu : workers) {
	        try {
	            wu.cleanUp();
	        }
	        catch (Exception e) {
	            JSONException.traceException(e, "While cleaning up worker: "+wu.workerId);
	        }
        }	    
	}
}
