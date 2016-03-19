package com.hp.de.automation.onramp.threading.v1;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.jdom.JDOMException;

import com.jcraft.jsch.JSchException;


public class OnRampJobSubmit {
	
	public static String printHref = null;
	

	  public static void main(String[] args) throws FileNotFoundException, HttpException, IOException, InterruptedException, JDOMException
	    
	  {
		  if(args == null || args.length < 2)
			{
				throw new RuntimeException("\nUsage :-  Java -jar OnRampJobSubmit.jar <stack properties file> <Data file>\n" +
						"\nExample :- Java -jar OnRampJobSubmit.jar dev02.properties DataSet.xlsx  \n" );
			}
		  ExcelOperations exc = new ExcelOperations();
		  String stackdetails = args[0];
		  String pstorefile = "printer.properties";;
		  String datafile = args[1];
		  OAuthGen ogen = new OAuthGen(stackdetails);
		  JschJdbc jjs = new JschJdbc(stackdetails);
		  String auth = null, pEmailAddress=null,authtype = null,useremail=null;
		  ArrayList< String > plist = new ArrayList< String >();
		  
//		  Actual Job submission
		  
		 
		  List sheetdata = exc.readexceldata(datafile, Integer.valueOf(0));
			
			
			for (int i = 1; i < sheetdata.size(); i++)
			{
				
				List testcase_row = (List)sheetdata.get(i);
				pEmailAddress = String.valueOf(testcase_row.get(8));
				authtype = String.valueOf(testcase_row.get(10));
				useremail = String.valueOf(testcase_row.get(11));
				auth = ogen.getAuth(stackdetails,pstorefile,pEmailAddress,authtype,useremail);
				new Jobsubmitter(stackdetails,datafile,pstorefile, testcase_row,auth);
				
				if ( !plist.contains( pEmailAddress ) ) 
		            plist.add( pEmailAddress ); 

				
			}
			
			/*for(int j=0; j< plist.size();j++){
				String pname = plist.get(j);
				System.out.println("Printer de tails = " + pname );
				String filename = pname +".xml";
				try {
					jjs.getJobResults(pname,filename);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
		  

	  }

	
	
	
	
}
