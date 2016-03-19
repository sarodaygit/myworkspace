package com.hp.de.automation.email;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;



public class EmailClient {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws MessagingException 
	 * @throws AddressException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, AddressException, MessagingException {
		
		
		if(args == null || args.length < 6)
		{
			throw new RuntimeException("\nUsage :-  Java -jar EmailClient.jar <mailservice> <proxy setting> <from address> <from password> <to address> <attachment> \n" +
					"\nExample :- Java -jar EmailClient.jar yahoo/gmail true/false from@xxx.com pxxxwxxd to@yyy.com \"C:/Temp/testdata.xlsx\" \n" );
		}
		
		String from_address = args[2];
		String from_password = args[3];
		
		String to_address = args[4];
		String att_path = args[5]; //"C:/nagaraj/Testfiles/Test/Glows_300dpi.bmp";
		Boolean proxystat = Boolean.valueOf(args[1]);
		String mailservice =  args[0];
		
		System.out.println("Mail service to used = " +mailservice + "\n" + "Is proxy enabled = "+ proxystat+"\n");
		
			EmailJobSubmiterTool mail =  new EmailJobSubmiterTool(mailservice, proxystat);
			System.out.println("Sending mail to " + to_address );
			mail.postEmail(from_address, from_password, to_address, att_path);
			System.out.println("Mail sent successfully");
		
			
		}
		
		
		

}
