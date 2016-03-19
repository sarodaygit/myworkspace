package com.hp.de.automation.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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



public class EmailJobSubmiterTool {
	
	
	
	Properties email_prop = new Properties();
	Properties sys_prop;
	
	
	
	public EmailJobSubmiterTool(String mailservice, Boolean proxyset) {
		
		super();
		sys_prop = System.getProperties();
		
		if(mailservice.equalsIgnoreCase("gmail")){
			try {
				email_prop.load(new FileInputStream("gmail.properties"));
				sys_prop.put("mail.transport.protocol", email_prop.getProperty("mail_transport_protocol"));
			    sys_prop.put("mail.smtp.starttls.enable", email_prop.getProperty("mail_smtp_starttls_enable"));
			    sys_prop.put("mail.smtp.host", email_prop.getProperty("mail_smtp_host"));
			    sys_prop.put("mail.smtp.auth", email_prop.getProperty("mail_smtp_auth"));
			    sys_prop.put("mail.smtp.port", email_prop.getProperty("mail_smtp_port"));
			    sys_prop.put("mail.smtp.debug", email_prop.getProperty("mail_smtp_debug"));
			    sys_prop.put("mail.smtp.socketFactory.port", email_prop.getProperty("mail_smtp_socketFactory_port"));
			    sys_prop.put("mail.smtp.socketFactory.fallback", email_prop.getProperty("mail_smtp_socketFactory_fallback"));
			    sys_prop.put("mail.smtp.socketFactory.class", email_prop.getProperty("mail_smtp_socketFactory_class"));
				
				
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
				email_prop.load(new FileInputStream("yahoo.properties"));
				sys_prop.put("mail.smtp.host", email_prop.getProperty("mail_smtp_host"));
			    sys_prop.put("mail.smtp.auth", email_prop.getProperty("mail_smtp_auth"));
			    sys_prop.put("mail.smtp.port", email_prop.getProperty("mail_smtp_port"));
			    sys_prop.put("mail.smtp.debug", email_prop.getProperty("mail_smtp_debug"));
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		if(proxyset == true){
	    	sys_prop.put("proxySet", email_prop.getProperty("proxySet"));
		    sys_prop.put("proxyHost", email_prop.getProperty("proxyHost"));
		    sys_prop.put("proxyPort", email_prop.getProperty("proxyPort"));
//		    sys_prop.put("socksProxyHost", email_prop.getProperty("socksProxyHost"));
		    sys_prop.put("socksProxyHost", email_prop.getProperty("proxyHost"));
	    
	    }

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
	
	void postEmail(String p_user, String p_pwd, String to, String atfile) throws AddressException, MessagingException{
		
		File file = new File(atfile);
		
		String subject = "Email with attachment " + file.getName()+" sent @ "+ CurrTime();
		
		Multipart multipart = new MimeMultipart();
		Authenticator auth = new MailAuthenticator(p_user,p_pwd);
        Session session = Session.getDefaultInstance(sys_prop, auth);
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(p_user));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        multipart =  mime_generator(atfile);
        message.setContent(multipart);
        Transport.send(message);

	}

	private MimeMultipart mime_generator(String f_attach_path) throws MessagingException {
		String encoding = "text/plain; " + "charset=us-ascii";
		String mailbody = "Hello Mail";
		MimeMultipart f_multipart = new MimeMultipart() ;
//		MimeBodyPart messageBodyPart = new MimeBodyPart();
		MimeBodyPart at_messageBodyPart = new MimeBodyPart();
	/*	messageBodyPart.setText(mailbody);
		messageBodyPart.setHeader("Content-Transfer-Encoding", "7bit");
		messageBodyPart.setContent(mailbody,encoding);
		f_multipart.addBodyPart(messageBodyPart);*/
		
		  DataSource source = new FileDataSource(f_attach_path);
		  DataHandler attfile =  new DataHandler(source);
	        at_messageBodyPart.setDataHandler(attfile);
	        String filename = new File(f_attach_path).getName();
			at_messageBodyPart.setFileName(filename);
			f_multipart.addBodyPart(at_messageBodyPart);
		return f_multipart;
	}


	private String CurrTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   //get current date time with Date()
		   Date date = new Date();
		   System.out.println(dateFormat.format(date));

		   //get current date time with Calendar()
		   Calendar cal = Calendar.getInstance();
		   System.out.println();
		   
		   return dateFormat.format(cal.getTime());
		   
		
	}

}
