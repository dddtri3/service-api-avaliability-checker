package com.drfirst.mis.service_api_avaliability_checker;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Allen Chiao
 *
 */
public class ResultMap {
	
	public static String KEY_STATUS="response.status";
	
	
	private Map<String, String> holder;
	
	public ResultMap() {
		this.holder = new HashMap<String, String>();
	}
	
	public ResultMap setStatus(String status) {
		this.holder.put(KEY_STATUS, status);
		return this;
	}
	
	public String getStatus() {
		return this.holder.get(KEY_STATUS);
	}

}
