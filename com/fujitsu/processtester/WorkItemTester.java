package com.fujitsu.processtester;

import java.net.URL;
import java.util.Random;

import com.purplehillsbooks.json.JSONArray;
import com.purplehillsbooks.json.JSONException;
import com.purplehillsbooks.json.JSONObject;

import com.fujitsu.processtester.data.StressTestConfig;
import com.fujitsu.processtester.data.UserConfig;

/**
 * Work Item data. <br />
 * Contains only data needed for making choice.
 * @author sawadary
 *
 */
public class WorkItemTester {

    public static RunStats makeChoiceStats = new RunStats("Make Choice");
    public static RunStats acceptStats = new RunStats("Accept Workitem");
    
    private Random _random = new Random(System.currentTimeMillis());
    
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Members 
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	private int _id;
	private int state;
	private String _instanceName;
	private String _name;
	private String[] _choices;
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Getters and setters
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}
	
	public String getInstanceName() {
		return _instanceName;
	}

	public void setInstanceName(String instanceName) {
		_instanceName = instanceName;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String[] getChoices() {
		return _choices;
	}

	public void setChoices(String[] choices) {
		_choices = choices;
	}
	
	public int getState() {
	    return state;
	}
	
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// Static functions 
	//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	/**
	 * Create an instance from JSON 
	 * @param json
	 * @return
	 * @throws JSONException 
	 */
	public WorkItemTester(JSONObject json) throws JSONException {
		
		setId(json.getInt("id"));
		setInstanceName(json.getString("instanceName"));
		setName(json.getString("name"));
		JSONArray jsonChoices = json.getJSONArray("choices");
		String[] choices = new String[jsonChoices.length()];
		for(int i=0; i<choices.length; i++) {
			choices[i] = jsonChoices.getString(i);
		}
		setChoices(choices);
		state = json.getInt("state");
	}
	
    public boolean acceptIt(StressTestConfig config, UserConfig uc) throws Exception {
        
        String authString = uc.getAuthString();
        long startTime = System.currentTimeMillis();
        try {
            String urlStr = config.getAppBaseUrl()+String.format("wi=%d/Accept", _id);
            URL url = new URL(urlStr);
            JSONObject postBody  = new JSONObject();
            JSONObject result = AA_Processor.sendHttpRequest(url, "POST", authString, postBody);
            if (!result.has("wi")) {
                throw new Exception("Result of Accept does not look like a work item record");
            }
            JSONObject wi = result.getJSONObject("wi");
            int state = wi.getInt("state");
            if (state!=8) {
                throw new Exception("Expected workitem to be accepted, but instead it is "+state);
            }
            acceptStats.recordPass(startTime);
            System.out.println(String.format("Workitem (%d) accepted by (%s)", _id, uc.getName()));
            return true;
        }
        catch (Exception e) {
            acceptStats.recordFail(startTime);
            if (JSONException.containsMessage(e, "The work item has already")) {
                System.out.println("Tester was too late to make accept workitem ("+_id+") -- ignoring failure message");
                return false;
            }
            else if (containsMessage(e, "The work item has already")) {
                System.out.println("Tester was too late to make accept workitem ("+_id+") -- ignoring failure message");
                return false;
            }
            else {
                throw new Exception("Tester was unable to accept workitem ("+_id+")", e);
            }
        }
    } 

    
    public boolean containsMessage(Throwable t, String fragment) {
        while (t!=null) {
            if (t.toString().contains(fragment)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
    
    public boolean declineIt(StressTestConfig config, UserConfig uc) throws Exception {
        
        String authString = uc.getAuthString();
        try {
            String urlStr = config.getAppBaseUrl()+String.format("wi=%d/Decline", _id);
            URL url = new URL(urlStr);
            JSONObject postBody  = new JSONObject();
            JSONObject result = AA_Processor.sendHttpRequest(url, "POST", authString, postBody);
            if (!result.has("wi")) {
                throw new Exception("Result of Decline does not look like a work item record");
            }
            JSONObject wi = result.getJSONObject("wi");
            int state = wi.getInt("state");
            if (state!=6) {
                throw new Exception("Expected workitem to be Decline, but instead it is "+state);
            }
            System.out.println(String.format("Workitem (%d) Decline by (%s)", _id, uc.getName()));
            return true;
        }
        catch (Exception e) {
            throw new Exception("Tester was unable to DECLINE workitem ("+_id+")", e);
        }
    } 
    
    public void modifyData(JSONObject uda) throws Exception {
        String product = uda.optString("Product", "Tennis Ball");
        String region = uda.optString("Region", "Overseas");
        String custType = uda.optString("Customer Type", "Advanced");
        
        if (uda.has("jsonRecord")) {
            JSONObject jo = uda.getJSONObject("jsonRecord");
            jo.put("Product", "Test "+product);
            jo.put("Region", "Test "+region);
            jo.put("CustomerType", "Test "+custType);
        }
        if (uda.has("jsonArray")) {
            JSONArray ja = uda.getJSONArray("jsonArray");
            boolean hasProd = false;
            boolean hadRegion = false;
            boolean hasCustomer = false;
            for (int i=0; i<ja.length(); i++) {
                String val = ja.getString(i);
                if (val.equalsIgnoreCase(product)) {
                    hasProd = true;
                }
                if (val.equalsIgnoreCase(region)) {
                    hadRegion = true;
                }
                if (val.equalsIgnoreCase(custType)) {
                    hasCustomer = true;
                }
            }
            if (!hasProd) {
                ja.put(product);
            }
            if (!hadRegion) {
                ja.put(region);
            }
            if (!hasCustomer) {
                ja.put(custType);
            }
        }
    }

    public void makeRandomChoice(StressTestConfig config, UserConfig uc) throws Exception {
        
        String authString = uc.getAuthString();
        int choiceIdx = (int) Math.floor(_random.nextDouble() * _choices.length);
        String choice = _choices[choiceIdx];
        long startTime = System.currentTimeMillis();
        int workItemId = this.getId();
        String urlWIStr = config.getAppBaseUrl()+String.format("wi=%d", workItemId);
        URL urlWi = new URL(urlWIStr);
        
        String urlMakeChoiceStr = config.getAppBaseUrl()+String.format("wi=%d/MakeChoice", workItemId);
        URL urlMakeChoice = new URL(urlMakeChoiceStr);
        
        try {
            JSONObject wiRecord = AA_Processor.sendHttpRequest(urlWi, "GET", authString, null);
            if (!wiRecord.has("pi")) {
                throw new Exception("Something wrong with the workitem, no 'pi' record: "+wiRecord.toString());
            }
            JSONObject pi = wiRecord.getJSONObject("pi");
            if (!pi.has("uda")) {
                throw new Exception("Something wrong with the pi, no 'uda' record: "+pi.toString());
            }
            JSONObject uda = pi.getJSONObject("uda");
            modifyData(uda);
           
            JSONObject wiJson = new JSONObject();
            wiJson.put("choice", choice);
            wiJson.put("uda", uda);
            
            JSONObject result = AA_Processor.sendHttpRequest(urlMakeChoice, "POST", authString, wiJson);
            if (!result.has("wi")) {
                throw new Exception("Result of MakeChioce does not look like a work item record");
            }
            JSONObject wi = result.getJSONObject("wi");
            int state = wi.getInt("state");
            if (state!=-1) {
                throw new Exception("Expected workitem to be completed, but instead it is "+state);
            }
            makeChoiceStats.recordPass(startTime);
        }
        catch (Exception e) {
            makeChoiceStats.recordFail(startTime);
            if (JSONException.containsMessage(e, "Could not retrieve the workitem")) {
                System.out.println("Tester was too late to make choice ("+choice+") on workitem ("+workItemId+") -- ignoring failure message");
            }
            else {
                throw new Exception("Tester was unable to make choice ("+choice+") on workitem ("+workItemId+")", e);
            }
        }
        System.out.println(String.format("Choice is made to \"%s\" on \"%s\" of \"%s\" by \"%s\"", choice, getName(),
                getInstanceName(), uc.getName()));
    } 
    
}
