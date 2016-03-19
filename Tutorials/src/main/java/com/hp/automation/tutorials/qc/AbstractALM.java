package com.hp.automation.tutorials.qc;

import java.util.Properties;

import org.apache.log4j.Logger;


/**
 * 
 * @author mavo
 * Abstract class to be used will all REST webservices - pls. add common functions here
 *
 */
public abstract class AbstractALM {
	
	public Logger cat;
	public ConfigurationContext ctx = CommonConfigurationContext.getInstance();
	public Properties prop = CommonProperties.getInstance();
	
	public  AbstractALM(){
		cat = CommonLog.getInstance(getClass());
		
	}
	public Logger getLogger(Class<?> cName){
		return cat;
	}

	public void errorHandler(Throwable a)  {
		String error="Webservice Error: "+a.getCause()+" "+a.getMessage();
		if (null == cat) cat = getLogger(getClass());
		cat.debug(error);
	}

}
