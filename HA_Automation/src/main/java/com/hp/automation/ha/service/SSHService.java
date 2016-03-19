package com.hp.automation.ha.service;

import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHService {
	
	private Session session;

	public SSHService(String userName, String password, String Host, int port) {
		JSch jsch = new JSch();
		Session session = null;
		Properties config = new Properties();
		try {
			session = jsch.getSession(userName, Host, port);
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);
			session.connect();
		} catch (JSchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.setSession(session);
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}
	
	public void disconnectSesstion() {
		
		session.disconnect();
		
	}
	

}
