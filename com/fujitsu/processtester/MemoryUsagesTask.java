package com.fujitsu.processtester;

import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;

import com.purplehillsbooks.json.JSONException;
import com.purplehillsbooks.json.JSONObject;

import com.fujitsu.processtester.data.StressTestConfig;

/**
 * Get memory usage information from AA and write it to the file.
 * 
 * @author sawadary
 *
 */
public class MemoryUsagesTask extends TimerTask {

	@Override
	public void run() {
		try {
			doTask();
		} catch (Exception ex) {
			JSONException.traceException(ex, "Failure while collection memory usages of the AA.");
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Members
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	private StressTestConfig _config;
	private String _logFileName;

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Methods
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Constructor
	 * 
	 * @param config
	 * @param fileName
	 *            path to the outputting file.
	 * @throws FileNotFoundException
	 */
	public MemoryUsagesTask(StressTestConfig config, String fileName) {
		_config = config;
		_logFileName = fileName;

		System.out.println("Writing memory usage task has been started.");

		// Write header line.
		writeLine("time, totalAllocatedMemory, freeMemory, maxMemory, usedMemory, totalFreeMemory");
	}

	/**
	 * Collect memory usage from the AA and dump it to the file.
	 */
	private void doTask() {
		try {
			// Ask AA for the memory usage
			JSONObject res = AA_Processor.sendHttpRequest(new URL(_config.getBaseUrl() + "api/"), "GET",
					_config.getUserRandom().getAuthString(), null);
			long totalAllocatedMemory = res.getLong("allocatedMemory");
			long freeMemory = res.getLong("freeMemory");
			long maxMemory = res.getLong("maxMemory");
			long usedMemory = totalAllocatedMemory - freeMemory;
			long totalFreeMemory = maxMemory - usedMemory;

			// Write all
			Calendar myCal = Calendar.getInstance();
			DateFormat myFormat = new SimpleDateFormat();

			writeLine(String.format("%s, %d, %d, %d, %d, %d", myFormat.format(myCal.getTime()), totalAllocatedMemory,
					freeMemory, maxMemory, usedMemory, totalFreeMemory));

		} catch (Exception e) {
			// TODO: Nice to give user more information. For now, it ignores because it is
			// not important
			System.out.println("getting memory usage has failed");
		}
	}

	/**
	 * Common function to write line
	 * 
	 * @param line
	 */
	private void writeLine(String line) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter out = null;
		try {
			fw = new FileWriter(_logFileName, true);
			bw = new BufferedWriter(fw);
			out = new PrintWriter(bw);
			out.println(line);
			out.close();
		} catch (IOException e) {
			System.out.println("writing memory usage has failed");
		} finally {
			if (out != null) {
				out.close();
			}
			try {
				if (bw != null)
					bw.close();
			} catch (IOException e) {
			}
			try {
				if (fw != null)
					fw.close();
			} catch (IOException e) {
			}
		}
	}

}
