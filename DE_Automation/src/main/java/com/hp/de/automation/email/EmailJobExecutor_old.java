package com.hp.de.automation.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;



public class EmailJobExecutor_old {
	
	
	public Properties getEnv(String mailservice,boolean proxyset) {

		Properties system_properties;
		system_properties = System.getProperties();
		Properties email_properties = new Properties();
		 
		
		if(mailservice.equalsIgnoreCase("gmail")){
			try {
				email_properties.load(new FileInputStream("gmail.properties"));
				system_properties.put("mail.transport.protocol", email_properties.getProperty("mail_transport_protocol"));
				system_properties.put("mail.smtp.starttls.enable", email_properties.getProperty("mail_smtp_starttls_enable"));
				system_properties.put("mail.smtp.host", email_properties.getProperty("mail_smtp_host"));
				system_properties.put("mail.smtp.auth", email_properties.getProperty("mail_smtp_auth"));
				system_properties.put("mail.smtp.port", email_properties.getProperty("mail_smtp_port"));
				system_properties.put("mail.smtp.debug", email_properties.getProperty("mail_smtp_debug"));
				system_properties.put("mail.smtp.socketFactory.port", email_properties.getProperty("mail_smtp_socketFactory_port"));
				system_properties.put("mail.smtp.socketFactory.fallback", email_properties.getProperty("mail_smtp_socketFactory_fallback"));
				system_properties.put("mail.smtp.socketFactory.class", email_properties.getProperty("mail_smtp_socketFactory_class"));
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} 
		else if(mailservice.equalsIgnoreCase("yahoo")){
			try {
				email_properties.load(new FileInputStream("yahoo.properties"));
				system_properties.put("mail.smtp.host", email_properties.getProperty("mail_smtp_host"));
				system_properties.put("mail.smtp.auth", email_properties.getProperty("mail_smtp_auth"));
				system_properties.put("mail.smtp.port", email_properties.getProperty("mail_smtp_port"));
				system_properties.put("mail.smtp.debug", email_properties.getProperty("mail_smtp_debug"));
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		if(proxyset == true){
			system_properties.put("proxySet", email_properties.getProperty("proxySet"));
			system_properties.put("proxyHost", email_properties.getProperty("proxyHost"));
			system_properties.put("proxyPort", email_properties.getProperty("proxyPort"));
//		    system_properties.put("socksProxyHost", email_prop.getProperty("socksProxyHost"));
			system_properties.put("socksProxyHost", email_properties.getProperty("proxyHost"));
	    
	    }

	return system_properties ;
	}
	
	private class MailAuthenticator extends javax.mail.Authenticator {
    	String suser =null;
    	String spwd = null ;
		public MailAuthenticator(String usr, String pwd) {
			suser = usr;
			spwd = pwd;
		}

		public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(suser, spwd);
        }
    }
	
	/*public MimeMultipart mime_generator(String mailbody_type,String mailbody, String f_attach_path) throws MessagingException {
		
		String encoding = null;
		
		if(mailbody_type.equalsIgnoreCase("html")) {
			encoding = "text/html; charset=ISO-8859-1";
		} else {
			encoding = "text/plain; charset=us-ascii";
		}
		 
		
		MimeMultipart f_multipart = new MimeMultipart() ;
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		MimeBodyPart at_messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(mailbody);
		messageBodyPart.setHeader("Content-Transfer-Encoding", "7bit");
		messageBodyPart.setContent(mailbody,encoding);
		f_multipart.addBodyPart(messageBodyPart);
		
		  DataSource source = new FileDataSource(f_attach_path);
		  DataHandler attfile =  new DataHandler(source);
	        at_messageBodyPart.setDataHandler(attfile);
	        String filename = new File(f_attach_path).getName();
			at_messageBodyPart.setFileName(filename);
			f_multipart.addBodyPart(at_messageBodyPart);
		return f_multipart;
	}
*/

	
public MimeMultipart mime_generatorall(String mailbody_type,String mailbody, String f_attach_path) throws MessagingException {
		
		String encoding = null;
		String[] attlist = f_attach_path.split(",");
		if(mailbody_type.equalsIgnoreCase("html")) {
			encoding = "text/html; charset=ISO-8859-1";
		} else {
			encoding = "text/plain; charset=us-ascii";
		}
		 
		
		MimeMultipart f_multipart = new MimeMultipart() ;
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(mailbody);
		messageBodyPart.setHeader("Content-Transfer-Encoding", "7bit");
		messageBodyPart.setContent(mailbody,encoding);
		f_multipart.addBodyPart(messageBodyPart);
		
		System.out.println("\nFollowing files are attached to email\n");
		 for(int i=0;i < attlist.length;i++){
			 System.out.println(attlist[i]);
			 MimeBodyPart at_messageBodyPart = new MimeBodyPart();
			 DataSource source = new FileDataSource(attlist[i]);
			 at_messageBodyPart.setDataHandler(new DataHandler(source));
			 at_messageBodyPart.setFileName(attlist[i]);
			 f_multipart.addBodyPart(at_messageBodyPart);
			 
		 }
		  
			
			
		return f_multipart;
	}
	
	
	public String CurrTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   //get current date time with Date()
		   Date date = new Date();
//		   System.out.println(dateFormat.format(date));

		   //get current date time with Calendar()
		   Calendar cal = Calendar.getInstance();
		   System.out.println();
		   
		   return dateFormat.format(cal.getTime());
		   
		
	}
	
	
	/*void postEmail(String mailservice,boolean proxyset,String from_username, String from_password, String to_address, String mailbody_type, String mailbody,String att_file ) 
			throws AddressException, MessagingException{
			
			File file = new File(att_file);
			Properties props = getEnv(mailservice,proxyset);
			String subject = "Email with attachment " + file.getName()+" sent @ "+ CurrTime();
			Multipart multipart = new MimeMultipart();
			Transport transport = null;
			Authenticator auth = new MailAuthenticator(from_username,from_password);
			Session session = Session.getInstance(props, auth);
			Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(from_username));
	        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to_address));
	        message.setSubject(subject);
	        multipart =  mime_generator(mailbody_type,mailbody,att_file);
	        message.setContent(multipart);
	        String provider;
		    Transport.send(message);
	       
		}*/
		
	void postEmailall(List tcList ) 
			throws AddressException, MessagingException{
		String mailservice =  null,from_username = null,  from_password = null, to_address = null, mailbody_type = null, mailbody = null, att_path = null;
		Boolean proxyset = true;
		
			mailservice = String.valueOf(tcList.get(0));
			proxyset = Boolean.valueOf(String.valueOf(tcList.get(1)));
			from_username = String.valueOf(tcList.get(2));
			from_password = String.valueOf(tcList.get(3));
			to_address = String.valueOf(tcList.get(4));
			mailbody_type = String.valueOf(tcList.get(5));
			mailbody = String.valueOf(tcList.get(6));
			att_path = String.valueOf(tcList.get(7));
			
			System.out.println("Mail service to used = " +mailservice + "\n"+
					"Proxy Settings = "+ proxyset+ "\n" + 
					"From Address = " + from_username+"\n" +
					"From password = " + from_password+"\n" +
					"To Address = " + to_address+ "\n"+
					"Email Body Type = " + mailbody_type+"\n"+
					"Email Body = " + mailbody+"\n"+
					"Attachment path = "+att_path + "\n"+
					"");
				
			File file = new File(att_path);
			Properties props = getEnv(mailservice,proxyset);
			String subject = "Email with attachment " + file.getName()+" sent @ "+ CurrTime();
			Multipart multipart = new MimeMultipart();
			Transport transport = null;
			Authenticator auth = new MailAuthenticator(from_username,from_password);
			Session session = Session.getInstance(props, auth);
			Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(from_username));
	        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to_address));
	        message.setSubject(subject);
	        multipart =  mime_generatorall(mailbody_type,mailbody,att_path);
	        message.setContent(multipart);
	        String provider;
		    Transport.send(message);
	       
		}	

}
