package com.hp.automation.ha.data;

public class HostData {
	
	private String userName;
	private String password;
	private String Host;
	private int port;
	
	public HostData(String userName, String password, String host, int port) {
		super();
		this.userName = userName;
		this.password = password;
		Host = host;
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return Host;
	}

	public void setHost(String host) {
		Host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
	

}
