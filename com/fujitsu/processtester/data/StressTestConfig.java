package com.fujitsu.processtester.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.purplehillsbooks.json.JSONArray;
import com.purplehillsbooks.json.JSONException;
import com.purplehillsbooks.json.JSONObject;

public class StressTestConfig {
	
    private static JSONObject root;
    
	/**
	 * Base Url of the demo server.
	 */
	private String _baseUrl = "http://interstagedemo:49950/aa/";
	/**
	 * Number of process to keep active during the test
	 */
	private int _runningProcessCount = 100;
	
	/**
	 * How often process count is being checked.
	 */
	private int _procCheckInterval = 5000;
	/**
	 * Every x milliseconds one task is completed. 
	 */
	private int _taskCompleteInterval = 500;

	/**
	 * The amount of time that a worker waits before checking the worlist again
	 */
    private int _worklistCheckTime = 10;
    
    
	/**
	 * String used for authentication <br />
	 * Format is "BASIC xxx" <- xxx is base64 encoded string <br />
	 * alex@us.fujitsu.com:Fujitsu1 
	 */
	private ArrayList<UserConfig> _userConfigs = new ArrayList<UserConfig>();
	/**
	 * Application name
	 */
	private String _appName = "RandomWalk";
	private String _tenant = "Default";
	/**
	 * Process definition ID
	 */
	private int processDefinition = 15;
	private String planName = "RandomWalk";
	
	/**
	 * Base process name when new instance is created. 
	 */
	private String _procInstanceName = "RandWkAutoTest_";
	
	String _adminAuth;
	
	/////////////////////////////////////////////////////////////////
	// GETTERS AND SETTERS
	/////////////////////////////////////////////////////////////////
	public String getBaseUrl() {
		return _baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
	    if (!baseUrl.endsWith("/")) {
	        throw new RuntimeException("baseUrl must end with a slash!");
	    }
		_baseUrl = baseUrl;
	}
	public int getRunningProcessCount() {
		return _runningProcessCount;
	}
	public void setRunningProcessCount(int runningProcessCount) {
		_runningProcessCount = runningProcessCount;
	}
	public int getProcCheckInterval() {
		return _procCheckInterval;
	}
	public void setProcCheckInterval(int procCheckInterval) {
		_procCheckInterval = procCheckInterval;
	}
	public int getTaskCompleteInterval() {
		return _taskCompleteInterval;
	}
	public void setTaskCompleteInterval(int taskCompleteInterval) {
		_taskCompleteInterval = taskCompleteInterval;
	}
	public ArrayList<UserConfig> getUserConfigs() {
		return _userConfigs;
	}
	
	public String getAppName() {
		return _appName;
	}
    public String getTenant() {
        return _tenant;
    }
    public int getProcDefId() {
		return processDefinition;
	}
    public String getPlanName() {
        return planName;
    }

    
    public String getProcInstanceName() {
		return _procInstanceName;
	}
	public void setProcInstanceName(String procInstanceName) {
		_procInstanceName = procInstanceName;
	}
	public int getWorklistCheckTime() {
	    return _worklistCheckTime;
	}
	public String getAdminAuth() {
	    return _adminAuth;
	}
	
	public JSONObject getGeneralObject() throws Exception {
	    return root.getJSONObject("general");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// General Methods
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	private static Random _random = new Random(System.currentTimeMillis());

	/**
	 * @return the full address to the tenant and application to access anything in that application
	 */
	public String getAppBaseUrl() {
	    return _baseUrl + "api/t=" + _tenant + "/a=" + _appName + "/";
	}
	
	/**
	 * Get an authentication string(randomly).
	 * 
	 * @param config
	 * @return
	 */
	public UserConfig getUserRandom() {
		ArrayList<UserConfig> users = this.getUserConfigs();
		int idx = (int) Math.floor(_random.nextDouble() * users.size());
		return users.get(idx);
	}

	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Gnerating an instance from a file
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	private static final String CONFIG_ROOT_GENERAL = "general";
	private static final String CONFIG_BASE_URL = "baseUrl";
	private static final String CONFIG_APP_ID = "appId";
	private static final String CONFIG_TENANT = "tenant";
	private static final String CONFIG_PROCESS_DEFINITION = "processDefinition";
	private static final String CONFIG_BASE_PROCESS_NAME = "baseProcessName";
	private static final String CONFIG_RUNNING_PROCESS_COUNT = "runningProcessCount";
	private static final String CONFIG_TASK_COMPLETION_INTERVAL = "taskCompletionInterval";

	private static final String CONFIG_USERS = "users";

	/**
	 * Reading from a configuration file.
	 * 
	 * @throws Exception
	 */
	public static StressTestConfig readFromFile(File file) throws Exception {

		try {
			StressTestConfig config = new StressTestConfig();

			root = JSONObject.readFromFile(file);
			config.readGeneralSection(root);
			config.readUsersSection(root);

			return config;
		} catch (Exception e) {
			throw new Exception("Failed to read config file at "+file, e);
		}
	}
	
	/**
	 * Read the section with the name "general"
	 * @param config
	 * @param root
	 * @throws JSONException
	 */
	private void readGeneralSection(JSONObject root) throws JSONException {
		JSONObject general = root.getJSONObject(CONFIG_ROOT_GENERAL);

		_baseUrl = general.optString(CONFIG_BASE_URL, "http://interstagedemo:49950/aa/");

		if (general.has(CONFIG_APP_ID)) {
		    _appName = general.getString(CONFIG_APP_ID);
		}

		if (general.has(CONFIG_TENANT)) {
		    _tenant = general.getString(CONFIG_TENANT);
		}

		if (general.has(CONFIG_PROCESS_DEFINITION)) {
		    processDefinition = general.getInt(CONFIG_PROCESS_DEFINITION);
		}
        if (general.has("planName")) {
            planName = general.getString("planName");
        }

		if (general.has(CONFIG_BASE_PROCESS_NAME)) {
			setProcInstanceName(general.getString(CONFIG_BASE_PROCESS_NAME));
		}

		if (general.has(CONFIG_RUNNING_PROCESS_COUNT)) {
			setRunningProcessCount(general.getInt(CONFIG_RUNNING_PROCESS_COUNT));
		}

		if (general.has(CONFIG_TASK_COMPLETION_INTERVAL)) {
			setTaskCompleteInterval(general.getInt(CONFIG_TASK_COMPLETION_INTERVAL));
		}
        if (general.has("adminAuthString")) {
            _adminAuth = UserConfig.convertToAuthString(general.getString("adminAuthString"));
        }
		
		
		_worklistCheckTime = general.optInt("worklistCheckTime", _worklistCheckTime);
	}

	/**
	 * 
	 * @param config
	 * @param root
	 * @throws Exception 
	 */
	private void readUsersSection(JSONObject root) throws Exception {

		JSONArray users = root.getJSONArray(CONFIG_USERS);
		for (int i = 0; i < users.length(); i++) {
			JSONObject anUser = users.getJSONObject(i);

			// retrieve authentication information
			_userConfigs.add(new UserConfig(anUser));
		}

		if (_userConfigs.size() == 0) {
			throw new Exception("Did not find any user settings in the config file.  Required to have one or more users configured.");
		}
	}
	

}
