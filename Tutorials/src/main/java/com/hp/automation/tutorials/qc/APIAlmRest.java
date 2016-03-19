package com.hp.automation.tutorials.qc;

import org.junit.Assert;
import org.junit.Test;



/**
 * ALM web service interaction JUnit tests.
 * Validate if the REST web service interaction is working properly.
 * 
 * @author Beat Hugelshofer (beat.hugelshofer@whitesoft.com)
 * @version 21th November 2011
 * @company WhiteSoft
 */
public class APIAlmRest {
	public static final String almServer = "http://dddd.dd.d.d.";
	
	@Test
	public void testGetInstance() throws Exception {
		// Create object
		ALMConnection alm = new ALMConnection();
		Assert.assertNotNull("ALM ojbect not created", alm);
	}
	
	private ALMConnection almPrepare() {
		ALMConnection alm = new ALMConnection();
		alm.setAlmUser("Whitesoft");
		alm.setAlmPassword("Whitesoft");
		alm.setAlmDomain("Training");
		alm.setAlmProject("Whitesoft");
		alm.setServerURL(almServer);
		alm.setServerPort(8080);
		
		return alm;
	}
	
	private boolean almAuthenticate(ALMConnection alm) {
		return alm.Authenticate();
	}
	
	@Test
	public void testAuthenticate() throws Exception {
		ALMConnection alm = almPrepare();
		Assert.assertTrue("Authentication failed", almAuthenticate(alm));
	}
	
	private boolean almStartSession(ALMConnection alm) {
		return alm.StartSession();
	}
	
	@Test
	public void testStartSession() throws Exception {
		ALMConnection alm = almPrepare();
		almAuthenticate(alm);
		Assert.assertTrue("Session not started", almStartSession(alm));
	}	

	@Test
	public void testLogout() throws Exception {
		ALMConnection alm = almPrepare();
		almAuthenticate(alm);
		almStartSession(alm);
		Assert.assertTrue("Not able to log out", alm.Logout());
	}
	
	@Test
	public void testAddVAPITestRun() throws Exception {
		ALMConnection alm = almPrepare();
		almAuthenticate(alm);
		almStartSession(alm);
		Assert.assertTrue("Not able to add results", alm.AddVAPITestRun(1, 2, 1, 3, "Whitesoft", "Passed", "1004"));
	}
	
	@Test
	public void buildBasicURL() {
		ALMConnection alm = new ALMConnection();
		alm.setServerURL(almServer);
		alm.setServerPort(8080);
		
		Assert.assertEquals(almServer+"/qcbin/", alm.buildBasicURL());
	}
	
	@Test
	public void buildFullURL() {
		ALMConnection alm = almPrepare();
		Assert.assertEquals(almServer+"/qcbin/", alm.buildFullURL());
		almAuthenticate(alm);
		Assert.assertEquals(almServer+"/qcbin/", alm.buildFullURL());
		almStartSession(alm);
		Assert.assertEquals(almServer+"/qcbin/rest/domains/Training/projects/Whitesoft/",
				alm.buildFullURL());
	}
	
}
