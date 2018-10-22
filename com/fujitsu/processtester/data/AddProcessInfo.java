package com.fujitsu.processtester.data;

public class AddProcessInfo {
	/**
	 * Application name. Used for REST API path. <br />
	 *  .../a=RandomWalk/...
	 */
	private String _appName;
	/**
	 * Process definition id. Used for REST API path. <br />
	 * .../pd=15/...
	 */
	private int _procDefId;
	/**
	 * Base name for a new process instance.<br />
	 * [baseName]=3
	 */
	private String _procBasename;
	/**
	 * Starting index no which will be used to create an instance name. <br />
	 * RandomWalkTest_[3]
	 */
	private int _idxStartNo;
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Getters and setters
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++		
	public String getAppName() {
		return _appName;
	}
	public void setAppName(String appName) {
		_appName = appName;
	}
	public int getProcDefId() {
		return _procDefId;
	}
	public void setProcDefId(int procDefId) {
		_procDefId = procDefId;
	}
	public String getProcBasename() {
		return _procBasename;
	}
	public void setProcBasename(String procBasename) {
		_procBasename = procBasename;
	}
	public int getIdxStartNo() {
		return _idxStartNo;
	}
	public void setIdxStartNo(int idxStartNo) {
		_idxStartNo = idxStartNo;
	}

}
