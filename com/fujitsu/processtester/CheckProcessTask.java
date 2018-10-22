package com.fujitsu.processtester;

import java.net.URL;
import java.util.TimerTask;

import com.purplehillsbooks.json.JSONException;
import com.purplehillsbooks.json.JSONObject;

import com.fujitsu.processtester.data.*;

/**
 * Task that make sure enough process is active
 * 
 * @author sawadary
 *
 */
public class CheckProcessTask extends TimerTask {

    public static RunStats createProcessStats = new RunStats("Create Process");

    
	/**
	 * Run task
	 */
	@Override
	public void run() {
		try {
			createProcessIfNeeded();
		} catch (Exception ex) {
            JSONException.traceException(ex,"Failure while trying to add a process.");
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Members
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	private StressTestConfig _config;
	private int _newProcessNo = -1;

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Methods
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Constructor
	 * 
	 * @param numProcessesToKeep
	 */
	public CheckProcessTask(StressTestConfig config) {
		_config = config;
	}

	/**
	 * check process count and create processes if necessary.
	 * 
	 * @throws Exception
	 */
	private void createProcessIfNeeded() throws Exception {
	    
	    //this is how many processes we are expecting
	    int expectedNumber = _config.getRunningProcessCount();

	    //request for a batch of at least the number we are expecting
		ProcessItem[] procs = AA_Processor.getAllProcessInstances(_config, expectedNumber+1);

		// figure out the no to assign on new instance. (first time only)
		// if the last item created on the last test is xxx_79, then we should start
		// from 80.
		if (_newProcessNo < 0) {
			int lastNo = findLastProcessNo(procs);
			_newProcessNo = lastNo + 1;
		}

		int numProcessToAdd = expectedNumber - procs.length;
		for (int i=0; i<numProcessToAdd; i++) {

			AddProcessInfo info = new AddProcessInfo();
			info.setAppName(_config.getAppName());
			info.setProcDefId(_config.getProcDefId());
			info.setProcBasename(_config.getProcInstanceName());
			info.setIdxStartNo(_newProcessNo);

			addProcess(_config, info);
			_newProcessNo++;
		}
	}

	/**
	 * Find the number that the automated test added last.
	 * 
	 * @param procs
	 * @return
	 */
	private int findLastProcessNo(ProcessItem[] procs) {
		int lastProcNo = -1;

		String basePrcName = _config.getProcInstanceName();
		for (ProcessItem pi : procs) {
			// ex. from "RandowWkAutoTest_3" -> extracting 3.
			if (pi.getName().startsWith(basePrcName)) {
				try {
					int no = Integer.parseInt(pi.getName().substring(basePrcName.length()));
					if (no > lastProcNo) {
						lastProcNo = no;
					}
				} catch (Exception ex) {
					// do nothing
				}
			}
		}
		return lastProcNo;
	}
	
	
    /**
     * Create and add new processes
     * 
     * @param baseUrl
     * @param authString
     * @param info
     * @param howMany
     * @throws Exception
     */
    public void addProcess(StressTestConfig config, AddProcessInfo info) throws Exception {
        long startTime = System.currentTimeMillis();
        String procName = info.getProcBasename() + (info.getIdxStartNo());
        try {
            String urlStr = config.getAppBaseUrl()+"/CreateInstance";
            URL url = new URL(urlStr);
    
            ProcessItem pi = ProcessFactory.createRandomly();
            pi.setName(procName);
            JSONObject jsonPi = pi.getAsJSON();
            jsonPi.put("planName", config.getPlanName());
    
            // TODO: check return value
            AA_Processor.sendHttpRequest(url, "POST", config.getUserRandom().getAuthString(), jsonPi);
            createProcessStats.recordPass(startTime);
            System.out.println(String.format("New process \"%s\" is added.", pi.getName()));
        }
        catch (Exception e) {
            createProcessStats.recordFail(startTime);
            throw new Exception("Unable to create a process instance");
        }
    }
    
}
