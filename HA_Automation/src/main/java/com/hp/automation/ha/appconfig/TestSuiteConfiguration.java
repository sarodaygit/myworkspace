package com.hp.automation.ha.appconfig;

import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public class TestSuiteConfiguration {
	private XMLConfiguration testsuiteConfiguration;
	private final Logger logger;
	
	public TestSuiteConfiguration(String testSuiteXml) {
		logger = Logger.getLogger("logger");
		Handler handler = new ConsoleHandler();
        logger.addHandler(handler);
        URL fileUrl = ClassLoader.getSystemResource(testSuiteXml);
        try {
            testsuiteConfiguration = new XMLConfiguration(fileUrl);
        } catch (ConfigurationException ex) {
            logger.warning(ex.toString());
        }
	}
	
	
	 public String getStackName(){
	        return testsuiteConfiguration.getString("stackName");        
	    }


	public String getprinterSimulatorHost() {
		// TODO Auto-generated method stub
		return testsuiteConfiguration.getString("printerSimulatorHost");
	}
	

}
