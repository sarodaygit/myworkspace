package com.hp.de.automation.onramp;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.httpclient.HttpException;
import org.jdom.JDOMException;


public class OnRampJobSubmit {
	
	public static String printHref = null;

	  public static void main(String[] args) throws FileNotFoundException, HttpException, IOException, InterruptedException, JDOMException
	    
	  {
		  if(args == null || args.length < 2)
			{
				throw new RuntimeException("\nUsage :-  Java -jar OnRampJobSubmit.jar <stack properties file> <Data file>\n" +
						"\nExample :- Java -jar OnRampJobSubmit.jar dev02.properties DataSet.xlsx  \n" );
			}
		  
		  String stackdetails = null;
		  String pstorefile = "printer.properties";;
		  String datafile = null;
		  stackdetails=args[0];
		  datafile=args[1];

		  
//		  Actual Job submission
		  Jobsubmitter job = new Jobsubmitter();
		  job.executor(stackdetails,datafile,pstorefile);

	  }

	
	
	
	
}
