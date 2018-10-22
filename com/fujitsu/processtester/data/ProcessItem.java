package com.fujitsu.processtester.data;

import com.purplehillsbooks.json.JSONObject;

public class ProcessItem {
    
    private static final String PI_ID_INST = "idInst";
    private static final String PI_NAME = "name";
    private static final String PI_STATE = "state";
    private static final String PI_UDA = "uda";
    private static final String PI_CUSTOMER_TYPE_NAME = "Customer Type";
    private static final String PI_CUSTOMER_TYPE_ID = "CustomerType";
    private static final String PI_PRODUCT = "Product";
    private static final String PI_REGION = "Region";
    private static final String PI_PRIORITY = "Priority";

    
	
	private int _id;
	
	private int _state;
	
	private String _name;
	
	private String _customerType;
	
	private String _product;
	
	private String _region;

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}

	public int getState() {
		return _state;
	}

	public void setState(int state) {
		_state = state;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getCustomerType() {
		return _customerType;
	}

	public void setCustomerType(String customerType) {
		_customerType = customerType;
	}

	public String getProduct() {
		return _product;
	}

	public void setProduct(String product) {
		_product = product;
	}

	public String getRegion() {
		return _region;
	}

	public void setRegion(String region) {
		_region = region;
	}
	
	public JSONObject getUDAValuesForCreate() throws Exception {
        JSONObject uda = new JSONObject();
        uda.put(PI_CUSTOMER_TYPE_ID, getCustomerType());
        uda.put(PI_PRODUCT, getProduct());
        uda.put(PI_REGION, getRegion());
        return uda;
	}
	
	public void setUDAValueFromList(JSONObject listItemUDA) throws Exception {
        setCustomerType(listItemUDA.getString(PI_CUSTOMER_TYPE_NAME));
        setProduct(listItemUDA.getString(PI_PRODUCT));
        setRegion(listItemUDA.getString(PI_REGION));	    
	}
	
    public ProcessItem()  {
        //default constructor does not need to do anything
    }
	public ProcessItem(JSONObject json) throws Exception {
        setId(json.getInt(PI_ID_INST));
        setName(json.getString(PI_NAME));
        setState(json.getInt(PI_STATE));
        JSONObject uda = json.getJSONObject(PI_UDA);
        setUDAValueFromList(uda);	    
	}
	
	public JSONObject getAsJSON() throws Exception {
        JSONObject jsonPi = new JSONObject();
        jsonPi.put(PI_NAME, getName());
        jsonPi.put(PI_PRIORITY, 8);
        jsonPi.put(PI_UDA, getUDAValuesForCreate());
        jsonPi.put("description", "Randomly created process originally for customer type "
            +getCustomerType()+", product "+getProduct()
            +", and region "+getRegion()+".");
        return jsonPi;
	}

}
