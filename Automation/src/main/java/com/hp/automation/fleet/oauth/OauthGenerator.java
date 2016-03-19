package com.hp.automation.fleet.oauth;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.httpclient.HttpException;



public class OauthGenerator {

	public static void main(String[] args) throws FileNotFoundException, HttpException, IOException {
		
		String stackname = args[0];
		String stackdetails=null;
		String pEmailAddress = args[1];
		String authtype = args[2];
		String useremail = args[3];
		String printer_Credentials = null;
		stackdetails = getStackPropertiesfile(stackname); 
		PrinterCredentials prncrdn = new PrinterCredentials(stackdetails);
		printer_Credentials = prncrdn.getPrnCredentials(pEmailAddress);
		OAuthGen ogen = new OAuthGen(stackdetails);
		String auth = ogen.getAuth(pEmailAddress ,authtype ,useremail);
	}

	

	private static String getStackPropertiesfile(String stackname) {
		Hashtable stacks = new Hashtable();
		stacks.put("DIS", "test1.properties");
		stacks.put("Test01", "test2.properties");
		stacks.put("PIE", "test3.properties");
		String stackdetails = (String) stacks.get(stackname);
		return stackdetails;
	}

}
