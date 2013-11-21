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
	
	public static String KEY_TESTCASE_NAME="response.testcase.name";
	
	
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
	
	public ResultMap setTestcaseName(String status) {
		this.holder.put(KEY_TESTCASE_NAME, status);
		return this;
	}
	
	public String getTestcaseName() {
		return this.holder.get(KEY_TESTCASE_NAME);
	}

}
