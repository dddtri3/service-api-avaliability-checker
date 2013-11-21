package com.drfirst.mis.service_api_avaliability_checker;

public class HostEntryImpl implements HostEntry {

	private String host;
	private String mac;
	private String time;
	private String authUser;
	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void setHost(String input) {
		this.host= input;
	}
	
	@Override
	public String getAuthUser() {
		return authUser;
	}

	@Override
	public void setAuthUser(String input) {
		this.authUser= input;
	}
	
	@Override
	public String getMac() {
		return mac;
	}
	
	@Override
	public void setMac(String input) {
		this.mac=input;

	}

	@Override
	public String getTime() {
		return time;
	}
	
	@Override
	public void setTime(String input) {
		this.time= input;

	}


}
