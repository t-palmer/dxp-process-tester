package com.fujitsu.processtester;

import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.purplehillsbooks.json.JSONException;

import com.fujitsu.processtester.data.StressTestConfig;

public class ProcessTest {
    
    
	/**
	 * Application Main
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
            String configFileName = "AA_StressTest.config";
    	    if (args.length>0) {
    	        configFileName = args[0];
    	        System.out.println("CONFIG FILE NAME: "+configFileName);
    	    }
    		ProcessTest pt = new ProcessTest();
			pt.start(configFileName);
		} catch (Exception e) {
			System.out.println("Execution ended with an error.");
			JSONException.traceException(e, "ProcessTest main level");
		}
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Methods
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Start testing. <br />
	 * Creating processes. Completing a task in every xx milliseconds.
	 */
	private void start(String configFileName) throws Exception {

		// Read from a configuration file
		File file = new File(configFileName);
		if (!file.exists()) {
			throw new Exception(String.format("Cound not find the config file in %s", file.getAbsolutePath()));
		}
		File resultfolder = new File("testResults");
		if (!resultfolder.exists()) {
		    resultfolder.mkdirs();
	        if (!resultfolder.exists()) {
	            throw new Exception("Failed to create the output folder: "+resultfolder);
	        }
		}
		
		StressTestConfig config = StressTestConfig.readFromFile(file);
		
		
		
		BasicApiTester basicApiTester = new BasicApiTester(config);
		basicApiTester.run();
		
		ApplicationApiTester applicationApiTester = new ApplicationApiTester(config);
		applicationApiTester.run();
		
		// Make sure we have enough process instances before starting.
		CheckProcessTask chkProcTask = new CheckProcessTask(config);
		chkProcTask.run();

		// Start Timer1 : This timer task checks process count periodically
		Timer procTimer = new Timer(true);
		int chkInterval = config.getProcCheckInterval();
		procTimer.scheduleAtFixedRate(chkProcTask, chkInterval, chkInterval);

		// Start Timer2 : This timer task make a choice for a work in every few seconds.
		Timer workerTimer = new Timer(true);
		int completionInterval = config.getTaskCompleteInterval();
		AllWorkers allWorkers = new AllWorkers(config);
		workerTimer.scheduleAtFixedRate(allWorkers, completionInterval, completionInterval);
		
		// Start Timer3: Collect memory usages. Activated only when user types "m"
		Timer memoryUsageTimer = null;

		// Keep moving until user types 'quit'
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("Enter 'quit' to exit, 's' for statistics, 'w' for workerlist, 'm' for start collecting memory usage");
			String str = scanner.nextLine();
			if ("quit".equalsIgnoreCase(str)) {
				System.out.println("Exiting!");
				break;
			}
			else if ("s".equalsIgnoreCase(str)) {
			    dumpStatistics(System.out);
			}
            else if ("w".equalsIgnoreCase(str)) {
                allWorkers.dumpWorkerStatus();
            }
            else if("m".equalsIgnoreCase(str)) {
            	
            	if( memoryUsageTimer == null ) {
            		// Start logging memory usages
            		// decide file name
            		Calendar myCal = Calendar.getInstance();
            		DateFormat myFormat = new SimpleDateFormat("MMddyyyy_HHmmss");
            		String memoryLogFile = resultfolder.getAbsolutePath() + "\\memory_log_" + myFormat.format(myCal.getTime()) + ".csv";
            		
            		// start timer;
	        		memoryUsageTimer = new Timer(true);
	        		MemoryUsagesTask memoryTask = new MemoryUsagesTask(config, memoryLogFile);
	        		memoryUsageTimer.scheduleAtFixedRate(memoryTask, 0, 1000 * 60 * 10);
            	}
            }
		}
		scanner.close();

		procTimer.cancel();
		workerTimer.cancel();
		if (memoryUsageTimer != null) {
			memoryUsageTimer.cancel();
		}
		System.out.println("Shutting down in 3 seonds....");
		Thread.sleep(3000);
		allWorkers.cleanUp();
		File outFile = new File(resultfolder, "testResults"+System.currentTimeMillis()+".txt");
		PrintStream outStream = new PrintStream(outFile);
		dumpStatistics(outStream);
		outStream.flush();
		outStream.close();
	}
	
	
    private static void dumpStatistics(PrintStream out) {
        CheckProcessTask.createProcessStats.dump(out);
        WorkingUser.getWorklistStats.dump(out);
        WorkItemTester.acceptStats.dump(out);
        WorkItemTester.makeChoiceStats.dump(out);
        BasicApiTester.testBasicAPIs.dump(out);
        ApplicationApiTester.testApplicationAPIs.dump(out);
    }

}
