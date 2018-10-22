package com.fujitsu.processtester;

import java.util.Date;
import java.util.Random;

import com.fujitsu.processtester.data.ProcessItem;

/**
 * Create a process instance with random value
 * @author sawadary
 *
 */
public class ProcessFactory {
	
	private static Random _random = new Random(new Date().getTime());
	
	private static String[] CUSTOMER_TYPES = new String[] {"MS", "Oracle", "Google", "Fujitsu", "Apple" };
	private static String[] REGIONS = new String[] {"CA", "NY", "NV", "TX"};
	private static String[] PRODUCT = new String[] {"Software", "desktop", "laptop", "phone", "watch" }; 
	
	/**
	 * Create a process with random value.
	 * @return
	 */
	public static ProcessItem createRandomly() {
		
		ProcessItem pi = new ProcessItem();
		
		int idx = (int)Math.floor(_random.nextDouble() * CUSTOMER_TYPES.length); 
		pi.setCustomerType(CUSTOMER_TYPES[idx]);
		idx = (int)Math.floor(_random.nextDouble() * REGIONS.length); 
		pi.setRegion(REGIONS[idx]);
		idx = (int)Math.floor(_random.nextDouble() * PRODUCT.length); 
		pi.setProduct(PRODUCT[idx]);
		
		return pi;
	}
}
