package com.hp.de.automation.email;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.hp.de.automation.email.ExcelOperations;


public class MultiEmailer_old {

	public static void main(String[] args) {
		
		
		ExcelOperations exc = new ExcelOperations();
		String datafile = args[0];
		List sheetdata = exc.readexceldata(datafile, Integer.valueOf(0));

		
		EmailJobExecutor_old mail =  new EmailJobExecutor_old();
		
		for (int i = 1; i < sheetdata.size(); i++) {
		 	System.out.println("Sending Email\n");
        	List testcase_row = (List)sheetdata.get(i);
			
			try {
				
				System.out.println("Row = " + i);
				
				mail.postEmailall(testcase_row);
				
			} catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Email Sent\n\n");
          }
			
			
			
			
			System.out.println("Done");
		
			
		}

	

}
