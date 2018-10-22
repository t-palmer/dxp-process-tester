package com.fujitsu.processtester.data;

import com.purplehillsbooks.json.JSONObject;

public class Application {
    
    private static final String APPLICATION_DESCRIPTION = "description";
    private static final String APPLICATION_HOME_PAGE = "homePage";
    private static final String APPLICATION_ID = "id";
    private static final String APPLICATION_NAME = "name";
    private static final String APPLICATION_STATE = "state";
    private static final String APPLICATION_ICON = "icon";
    private static final String APPLICATION_OWNER = "owner";
    private static final String APPLICATION_TENANT = "tenant";   
	
	private String _id;
	private String _description;	
	private String _name;
	private String _homepage;
	private int _state;
	private String _owner;
	private String _tenant;
	private String _icon;
	
	public String getId() {
		return _id;
	}

	public void setId(String _id) {
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
	
	public String getOwners() {
		return _owner;
	}

	public void setOwner(String _owner) {
		this._owner = _owner;
	}

	public int getState() {
		return _state;
	}

	public void setState(int _state) {
		this._state = _state;
	}

	public String getTenant() {
		return _tenant;
	}

	public void setTenant(String _tenant) {
		this._tenant = _tenant;
	}
	
	public String getHomepage() {
		return _homepage;
	}

	public void setHomepage(String _homepage) {
		this._homepage = _homepage;
	}
	
	public String getIcon() {
		return _icon;
	}

	public void setIcon(String _icon) {
		this._icon = _icon;
	}


	
    public Application()  {
        //default constructor does not need to do anything
    }
	public Application(JSONObject json) throws Exception {
        setId(json.getString(APPLICATION_ID));
        setName(json.getString(APPLICATION_NAME));
        setDescription(json.getString(APPLICATION_DESCRIPTION)); 
        setHomepage(json.getString(APPLICATION_HOME_PAGE));
        setState(json.getInt(APPLICATION_STATE));
        setTenant(json.getString(APPLICATION_TENANT));
        setIcon(json.getString(APPLICATION_ICON));
        setOwner(json.getString(APPLICATION_OWNER));
	}
	
	public JSONObject getAsJSON() throws Exception {
        JSONObject jsonTenant = new JSONObject();
        jsonTenant.put(APPLICATION_NAME, getName());
        jsonTenant.put(APPLICATION_ID,getId());
        jsonTenant.put(APPLICATION_DESCRIPTION, getDescription());
        jsonTenant.put(APPLICATION_ICON,getIcon());
        jsonTenant.put(APPLICATION_STATE, getState());
        jsonTenant.put(APPLICATION_TENANT,getTenant());
        jsonTenant.put(APPLICATION_STATE, getState());
        jsonTenant.put(APPLICATION_HOME_PAGE,getHomepage());
          
        return jsonTenant;
	}

}
