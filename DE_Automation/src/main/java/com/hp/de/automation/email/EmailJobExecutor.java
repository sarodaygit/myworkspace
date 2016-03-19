package com.hp.de.automation.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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



public class EmailJobExecutor {
	
	
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
			
		} else if(mailservice.equalsIgnoreCase("live")){
			try {
				email_properties.load(new FileInputStream("live.properties"));
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
			
		} else if(mailservice.equalsIgnoreCase("aol")){
			try {
				email_properties.load(new FileInputStream("aol.properties"));
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
			System.out.println(email_properties.getProperty("proxyHost"));
	    
	    }

	return system_properties ;
	}
	
	private class MailAuthenticator extends javax.mail.Authenticator {
    	String suser =null;
    	String spwd = null ;
		public MailAuthenticator(String usr, String pwd) {
			suser = usr;
			spwd = pwd;
			System.out.println("login username :- " + usr +"\tlogin password :- " + pwd);
		}

		public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(suser, spwd);
        }
    }

	public MimeMultipart mime_generatorall(String mailbody_type,String mailbody, String f_attach_path) throws MessagingException {
			
			MimeMultipart f_multipart = new MimeMultipart() ;
			
			String[] attlist = null;
			
			MimeBodyPart messageBodyPart = null;
			
			messageBodyPart = add_emailbody(mailbody_type, mailbody);
			f_multipart.addBodyPart(messageBodyPart);	
			attlist = f_attach_path.split(",");
			
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

	public MimeMultipart mime_generatoratt(String f_attach_path) throws MessagingException {
		
		MimeMultipart f_multipart = new MimeMultipart() ;
		String[] attlist = null;
		
		attlist = f_attach_path.split(",");
		
		
		
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
	
	public MimeMultipart mime_generatorbody(String mailbody_type,String mailbody) throws MessagingException {
		
			MimeMultipart f_multipart = new MimeMultipart() ;
			
			MimeBodyPart messageBodyPart = add_emailbody( mailbody_type,  mailbody);
			f_multipart.addBodyPart(messageBodyPart);	
			return f_multipart;
	}

	private MimeBodyPart add_emailbody( String mailbody_type, String mailbody) throws MessagingException {
		String encoding;
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		if(mailbody_type.equalsIgnoreCase("html")) {
			encoding = "text/html; charset=ISO-8859-1";
		} else {
			encoding = "text/plain; charset=us-ascii";
		}
		messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(mailbody);
		messageBodyPart.setHeader("Content-Transfer-Encoding", "7bit");
		messageBodyPart.setContent(mailbody,encoding);
		return messageBodyPart;
		
	}
			
	public String CurrTime(){
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			   //get current date time with Date()
//			   Date date = new Date();
	//		   System.out.println(dateFormat.format(date));
	
			   //get current date time with Calendar()
			   Calendar cal = Calendar.getInstance();
			   System.out.println();
			   
			   return dateFormat.format(cal.getTime());
			   
			
		}
	
	void postEmailall(List tcList ) 
			throws AddressException, MessagingException, FileNotFoundException{
		String mailservice =  null,from_username = null,  from_password = null, to_address = null, mailbody_type = null, mailbody = null, att_path = null, Testid = null;
		Boolean proxyset = true;
			Testid = String.valueOf(tcList.get(0));
			mailservice = String.valueOf(tcList.get(1));
			proxyset = Boolean.valueOf(String.valueOf(tcList.get(2)));
			from_username = String.valueOf(tcList.get(3));
			from_password = String.valueOf(tcList.get(4));
			to_address = String.valueOf(tcList.get(5));
			mailbody_type = String.valueOf(tcList.get(6));
			mailbody = String.valueOf(tcList.get(7));
			att_path = String.valueOf(tcList.get(8));
			
//			System.out.println("Mail service to used = " +mailservice + "\t"+ "Proxy Settings = "+ proxyset+ "\t" + "From Address = " + from_username+"\t" + 
//			"From password = " + from_password+"\t" + "To Address = " + to_address+ "\t"+ "Email Body Type = " + mailbody_type+"\t"+ "Email Body = " + mailbody+"\t"+ "Attachment path = "+att_path + "\n");
			
			File file = new File(att_path);
			Properties props = getEnv(mailservice,proxyset);
			String subject = CurrTime() + " - " + Testid + " from " + mailservice;
			Multipart multipart = new MimeMultipart();
			Transport transport = null;
			Authenticator auth = new MailAuthenticator(from_username,from_password);
			Session session = Session.getInstance(props, auth);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from_username));
	        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to_address));
	        message.setSubject(subject);
	        
	        
	       if((((mailbody_type.isEmpty()||mailbody_type.equalsIgnoreCase("")) && (mailbody.isEmpty()||mailbody.equalsIgnoreCase("")) && (att_path.isEmpty()||att_path.equalsIgnoreCase("")) )
					||
					((!mailbody_type.isEmpty()|| !mailbody_type.equalsIgnoreCase("")) && (mailbody.isEmpty()||mailbody.equalsIgnoreCase("")) && (att_path.isEmpty()||att_path.equalsIgnoreCase("")) )) )
					{
						System.out.println("Nessecary fields are empty . Cannot send mail\n");
						
			} else if(((mailbody_type.isEmpty()||mailbody_type.equalsIgnoreCase("")) && (mailbody.isEmpty()||mailbody.equalsIgnoreCase("")) &&  (!att_path.isEmpty()|| !att_path.equalsIgnoreCase("")))
							||
							((!mailbody_type.isEmpty()|| !mailbody_type.equalsIgnoreCase("")) && (mailbody.isEmpty()||mailbody.equalsIgnoreCase("")) &&  (!att_path.isEmpty()|| !att_path.equalsIgnoreCase(""))))
			{
				System.out.println("Sending only attachment " + subject +"\n");	
				multipart = mime_generatoratt( att_path);
				message.setContent(multipart);
				Transport.send(message);
						
			} else if((!mailbody_type.isEmpty()|| !mailbody_type.equalsIgnoreCase("")) && (! mailbody.isEmpty()|| !mailbody.equalsIgnoreCase("")) && (att_path.isEmpty()|| att_path.equalsIgnoreCase("")) )
					{
				System.out.println("Sending only body "+ subject +"\n");
				multipart = mime_generatorbody(mailbody_type, mailbody);
				message.setContent(multipart);
				Transport.send(message);

			} else {
				System.out.println("Sending both body and attachment "+ subject +"\n");
				multipart = mime_generatorall(mailbody_type, mailbody, att_path);
				message.setContent(multipart);
				Transport.send(message);
				
			}
					
	       
		}	

}
