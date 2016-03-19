package com.hp.de.automation.email;

import java.io.FileNotFoundException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.hp.de.automation.email.ExcelOperations;


public class MultiEmailer {

	public static void main(String[] args) throws FileNotFoundException {
		
		
		ExcelOperations exc = new ExcelOperations();
		String datafile = args[0];
		List sheetdata = exc.readexceldata(datafile, Integer.valueOf(0));
		String mailservice =  null,from_username = null,  from_password = null, to_address = null, mailbody_type = null, mailbody = null, att_path = null, Testid = null;
		
		
		EmailJobExecutor mail =  new EmailJobExecutor();
		
		for (int i = 1; i < sheetdata.size(); i++) {
//		 	System.out.println("Sending Email\n");
        	List testcase_row = (List)sheetdata.get(i);
        	Boolean proxyset = true;
			Testid = String.valueOf(testcase_row.get(0));
			mailservice = String.valueOf(testcase_row.get(1));
			proxyset = Boolean.valueOf(String.valueOf(testcase_row.get(2)));
			from_username = String.valueOf(testcase_row.get(3));
			from_password = String.valueOf(testcase_row.get(4));
			to_address = String.valueOf(testcase_row.get(5));
			
		/*	System.out.println("Testid"+Testid+"\n");
			System.out.println("mailservice"+mailservice+"\n");
			System.out.println("proxyset"+proxyset+"\n");
			System.out.println("from_username"+from_username+"\n");
			System.out.println("from_password"+from_password+"\n");
			System.out.println("to_address"+to_address+"\n");*/
			
			try {
				System.out.println("\n*****************************************************************");
//				System.out.println("\nRow = " + i);
				mail.postEmailall(testcase_row);
				Thread.sleep(60000);
				System.out.println("\n*****************************************************************\n");
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			System.out.println("Email Sent\n\n");
          }
			
			
			
			
			System.out.println("Done");
		
			
		}

	

}
