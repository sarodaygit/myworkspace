package com.hp.automation.ha.hatests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.hp.automation.ha.appconfig.AppConfiguration;
import com.hp.automation.ha.data.PRData;


public class HATestCase1 {

	private AppConfiguration appConfig;
	// private SSHSessionData sshSessionData;
	private PRData prd;

	@BeforeClass
	public void preRequsite() {
		/*
		 * String userName = null; String password = null; int port = 0; String
		 * host = null; this.sshSessionData = new SSHSessionData(userName,
		 * password, host, port);
		 */

		this.appConfig = new AppConfiguration("test01-config.xml");
		this.prd = new PRData(appConfig);

	}

	@Test
	public void shutdownService() {
	}

	@Test
	public void CheckStatus() {

	}

	@Test
	public void submitJob() {
		System.out.println(prd.getPrinterEmailId());

	}

	@Test
	public void startService() {

	}

}
