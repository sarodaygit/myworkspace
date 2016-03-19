package com.hp.automation.ha.automation;

import com.hp.automation.ha.appconfig.AppConfiguration;
import com.hp.automation.ha.data.PRData;

/**
 * Hello world!
 * 
 */
public class App {

	public static void main(String[] args) {
		
		AppConfiguration appconfig = new AppConfiguration("configuration.xml") ;
		String stackname =  appconfig.getStackName();
		System.out.println("stackname = " + stackname);
		
		PRData prd = new PRData(appconfig);
		prd.getPrinterEmailAddress();
		
		/*
		 * JSch jsch = new JSch(); Session session = null; Properties config =
		 * new Properties(); UserInfo ui = null; Channel channel = null;
		 * OutputStream output = new FileOutputStream("file1.txt");
		 * 
		 * try { // jsch.addIdentity("C:/nagaraj/dev.ppk"); session =
		 * jsch.getSession("saroday", "lin-saroday.asiapacific.hpqcorp.net",
		 * 22); config.put("StrictHostKeyChecking", "no");
		 * session.setConfig(config); session.setPassword("iso*help");
		 * session.connect(); channel = session.openChannel("shell");
		 * 
		 * 
		 * 
		 * OutputStream inputstream_for_the_channel = channel.getOutputStream();
		 * PrintStream commander = new PrintStream(inputstream_for_the_channel,
		 * true);
		 * 
		 * channel.setOutputStream(System.out,true); //
		 * channel.setOutputStream(output,true); channel.connect();
		 * commander.println("pwd");
		 * commander.println("cd /home/saroday/nagaraj");
		 * commander.println("sudo su"); commander.println("iso*help");
		 * commander.println("whoami"); commander.println("ls -al");
		 * channel.setOutputStream(output); System.out.println("**********\n" +
		 * output.toString()); do { Thread.sleep(1000); }
		 * while(channel.isEOF());
		 * 
		 * 
		 * 
		 * commander.println("exit"); channel.disconnect();
		 * session.disconnect();
		 * 
		 * 
		 * } catch (JSchException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * 
		 * if(channel.isConnected() || session.isConnected()){
		 * System.out.println("still connected"); } else {
		 * System.out.println("disconnected"); }
		 * 
		 * // TODO Auto-generated method stub
		 */

	}

}
