package com.hp.de.automation.onramp.threading.v2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.tools.ant.Main;

import com.jcraft.jsch.JSchException;



public class MultiThreadJobSubmit {

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
    	
    	 if(args == null || args.length < 2)
			{
				throw new RuntimeException("\nUsage :-  Java -jar OnRampJobSubmit.jar <stack properties file> <Data file>\n" +
						"\nExample :- Java -jar OnRampJobSubmit.jar dev02.properties DataSet.xlsx  \n" );
			}
    	 
    	 System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
    	 Log log = LogFactory.getLog(MultiThreadJobSubmit.class);
    	 
//    	 log.info("You do not want to see me");
    	 
    	 log.info("trying to open Excel");
		  ExcelOperations exc = new ExcelOperations();
		  
		  String stackdetails = args[0];
		  String pstorefile = "printer.properties";;
		  String datafile = args[1];
		  OAuthGen ogen = new OAuthGen(stackdetails);
		  JschJdbc jjs = new JschJdbc(stackdetails);
		  String auth = null, pEmailAddress=null,authtype = null,useremail=null;
		  ArrayList< String > plist = new ArrayList< String >();
		  List sheetdata = exc.readexceldata(datafile, Integer.valueOf(0));
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 1; i < sheetdata.size(); i++) {
        	List testcase_row = (List)sheetdata.get(i);
			pEmailAddress = String.valueOf(testcase_row.get(8));
			authtype = String.valueOf(testcase_row.get(10));
			useremail = String.valueOf(testcase_row.get(11));
			auth = ogen.getAuth(stackdetails,pstorefile,pEmailAddress,authtype,useremail);
			 Runnable worker = new JobExecutor(stackdetails,datafile,pstorefile, testcase_row,auth);
	            executor.execute(worker);
			
			
			if ( !plist.contains( pEmailAddress ) ) 
	            plist.add( pEmailAddress ); 

          }
        executor.shutdown();
     /*   while (!executor.isTerminated()) {
//        System.out.println("");
        }*/
        
        if(executor.awaitTermination(10,TimeUnit.MINUTES))
        System.out.println("Finished all threads");
        for(int j=0; j< plist.size();j++){
		String pname = plist.get(j);
		System.out.println("Printer details = " + pname );
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
	}
  
        
    }

}