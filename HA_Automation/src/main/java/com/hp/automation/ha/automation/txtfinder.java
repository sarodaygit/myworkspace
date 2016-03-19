package com.hp.automation.ha.automation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class txtfinder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		    findstatus("file1.txt");
	}

	private static void findstatus(String filepath) {
		BufferedReader br;
		String status1 = "running";
		String status2 = "Starting";
		String status3 = "Stopping";
		String status = null;
		String line = "";
   

		try {
		    br = new BufferedReader(new FileReader(filepath));
		    try {
		        while((line = br.readLine()) != null)
		        {
		        	
		        	
		            if((line.indexOf(status1)) != - 1){
		               System.out.println(status1 +" found");
		               status = status1;
		            }else if((line.indexOf(status2)) != - 1){
		            	System.out.println(status2 +" found");
		                   status = status2;
		            } else if((line.indexOf(status3)) != - 1){
		            	System.out.println(status3 +" found");
		                   status = status3;
		            }  else {
		            	System.out.println("None of the status found");
		            	status = "xxxx";
		            }
		        }
		        br.close();
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	}

}
