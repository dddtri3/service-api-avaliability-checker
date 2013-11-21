package com.drfirst.mis.service_api_avaliability_checker;

public interface HostEntry {

	public String getHost();
	
	public String getMac();
	
	public String getTime();
	
	public String getAuthUser();

	void setHost(String input);

	void setMac(String input);

	void setTime(String input);

	void setAuthUser(String input);

}
