package com.fujitsu.processtester.data;

import java.io.IOException;

import com.purplehillsbooks.json.JSONObject;
import com.purplehillsbooks.streams.Base64;

/**
 * User configurations
 * @author sawadary
 *
 */
public class UserConfig {
    private static final String CONFIG_NAME = "name";
    private static final String CONFIG_AUTH_STRING = "authString";
    
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Members
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	private String _name;
	private String _authString;
	private int _defaultWorkingTime;
	private int _duplicateWorkers;
	
	//this is a map from special activity names to work times (in seconds)
	private JSONObject workTimes;

	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Getters and setters
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		_name = name;
	}
	public String getAuthString() {
		return _authString;
	}
	public void setAuthString(String authString) {
		_authString = authString;
	}
	
	/**
	 * time in seconds for a particular activity
	 */
	public int getWorkingTime(String activityName) {
        return workTimes.optInt(activityName, _defaultWorkingTime);
	}
    /**
     * number of workers that should be created on this config
     */
    public int getDuplicateWorkers() {
        return _duplicateWorkers;
    }
	
    public UserConfig(JSONObject anUser) throws Exception {
        String authString = anUser.getString(CONFIG_AUTH_STRING);
        setAuthString(convertToAuthString(authString));

        // retrieve name to display
        if (anUser.has(CONFIG_NAME)) {
            setName(anUser.getString(CONFIG_NAME));
        } else {
            setName(getNameFromAuthString(authString));
        }
        
        _defaultWorkingTime = anUser.optInt("defaultWorkTime", 30);
        _duplicateWorkers   = anUser.optInt("duplicateWorkers", 1);
        if (anUser.has("workTimes")) {
            workTimes = anUser.getJSONObject("workTimes");
        }
        else {
            workTimes = new JSONObject();
        }
    }
	
    /**
     * Convert a readable authentication string into actual Base64 encoded format
     * string.
     * 
     * @param authString
     *            Authentication String. ex. alex@example.com:Fujitsu1
     * @return \"Basic xxxx-encodedString-xxxx \"
     */
    public static String convertToAuthString(String authString) {
        return String.format("Basic %s", Base64.encode(authString.getBytes()));
    }
    /**
     * Decode Base64 encoded part from the authentication string. Then find the name
     * part from it.
     * 
     * @param authString
     * @return
     */
    private static String getNameFromAuthString(String authString) {
        // Basic xxxxxx    ->  xxxxxx
        String decodedPart = authString.substring(authString.indexOf(' ') + 1);
        try {
            // xxxxx -> alex@example.com:Fujitsu1
            String decodedString = new String(Base64.decodeBuffer(decodedPart));
            
            // -> alex@example.com
            String name = decodedString.substring(0, decodedString.indexOf(':'));
            
            // extracting 'alex' part if the string is email
            int atMarkPos = name.indexOf('@');
            if(atMarkPos > 0) {
                return name.substring(0, atMarkPos);
            }
            return name;
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return "Unknown user";
        }
    }
	
}
