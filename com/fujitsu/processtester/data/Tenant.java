package com.fujitsu.processtester.data;

import com.purplehillsbooks.json.JSONObject;

public class Tenant {
    
    private static final String TENANT_CURRENT_USERS = "currentUsers";
    private static final String TENANT_DESCRIPTION = "description";
    private static final String TENANT_ID = "id";
    private static final String TENANT_NAME = "name";
    private static final String TENANT_STATE = "state";
    private static final String TENANT_USER_LIMIT = "userLimit";   
	
	private int _id;
	private String _description;	
	private String _name;
	private int _currentUsers;
	private int _state;
	private int _userLimit;
	
	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String _description) {
		this._description = _description;
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}
	
	public int getCurrentUsers() {
		return _currentUsers;
	}

	public void setCurrentUsers(int _currentUsers) {
		this._currentUsers = _currentUsers;
	}

	public int getState() {
		return _state;
	}

	public void setState(int _state) {
		this._state = _state;
	}

	public int getUserLimit() {
		return _userLimit;
	}

	public void setUserLimit(int _userLimit) {
		this._userLimit = _userLimit;
	}


	
    public Tenant()  {
        //default constructor does not need to do anything
    }
	public Tenant(JSONObject json) throws Exception {
        setId(json.getInt(TENANT_ID));
        setName(json.getString(TENANT_NAME));
        setDescription(json.getString(TENANT_DESCRIPTION)); 
        setCurrentUsers(json.getInt(TENANT_CURRENT_USERS));
        setState(json.getInt(TENANT_STATE));
        setUserLimit(json.getInt(TENANT_USER_LIMIT));
	}
	
	public JSONObject getAsJSON() throws Exception {
        JSONObject jsonTenant = new JSONObject();
        jsonTenant.put(TENANT_NAME, getName());
        jsonTenant.put(TENANT_ID,getId());
        jsonTenant.put(TENANT_DESCRIPTION, getDescription());
        jsonTenant.put(TENANT_CURRENT_USERS,getCurrentUsers());
        jsonTenant.put(TENANT_STATE, getState());
        jsonTenant.put(TENANT_USER_LIMIT,getUserLimit());
          
        return jsonTenant;
	}

}
