package com.hp.automation.fleet.oauth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.apache.commons.httpclient.HttpException;



public class PrinterCredentials {
	
	Properties server_prop = new Properties();
	Properties printer_details = new Properties();
	DBConnector dbc;
	String shardurl;
	
	public PrinterCredentials(String spropFileName) throws FileNotFoundException, IOException {
		dbc = new DBConnector(spropFileName);
		
		
		
	}
	
	
	public String getPrnCredentials(String pEmailAddress)
			throws IOException, FileNotFoundException, HttpException {
		
		String pstorefile = "printerid.properties";
		String  key = null, prn_credential=null;
		FileInputStream f = new FileInputStream(new File(pstorefile));
		printer_details.load(f);
		if(printer_details.getProperty(pEmailAddress) == null) {
			shardurl = dbc.getsharddetails(pEmailAddress);
			prn_credential = dbc.getCredentials(shardurl, pEmailAddress);
			key = pEmailAddress;
			storecredentials(pstorefile, key, prn_credential);
		}else {
			prn_credential = printer_details.getProperty(pEmailAddress);
			System.out.println("printer credentials is present for the printer \n" + prn_credential);
			
			
			
		}
		
		
		return prn_credential;

}


	public String storecredentials(String pstorefile, String pEmailAddress,
			String prn_crd) throws IOException {
		Properties prop = new Properties();
		FileInputStream ff = new FileInputStream(new File(pstorefile));
		PrintWriter out = new PrintWriter(new FileWriter(pstorefile, true));
		System.out.println("Storing the printer credentials for future reference");
		prop.setProperty(pEmailAddress, prn_crd);
		prop.store(out, null);
		out.close();
		// f.close();
		prop.load(ff);
		 ff.close();
		
		return prop.getProperty(pEmailAddress);

	}
}
