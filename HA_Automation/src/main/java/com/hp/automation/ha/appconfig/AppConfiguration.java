package com.hp.automation.ha.appconfig;

import java.net.URL;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public class AppConfiguration {
	private XMLConfiguration configuration;
	private final Logger logger;
	
	public AppConfiguration(String appConfigXml) {
		logger = Logger.getLogger("logger");
		Handler handler = new ConsoleHandler();
        logger.addHandler(handler);
        URL fileUrl = ClassLoader.getSystemResource(appConfigXml);
        try {
            configuration = new XMLConfiguration(fileUrl);
        } catch (ConfigurationException ex) {
            logger.warning(ex.toString());
        }
	}
	
	
	 public String getStackName(){
	        return configuration.getString("stackName");        
	    }


	public String getprinterSimulatorHost() {
		// TODO Auto-generated method stub
		return configuration.getString("printerSimulatorHost");
	}
	

}
