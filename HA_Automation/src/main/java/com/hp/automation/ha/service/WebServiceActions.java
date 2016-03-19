package com.hp.automation.ha.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.hp.automation.enums.ActionsEnums;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class WebServiceActions {
	
	private static Session session;
	

	public WebServiceActions(Session sshConnection) {
		this.session= sshConnection;
	}

	public String  webServiceCommandExecutor(ActionsEnums status) {

		Channel channel = null;
		OutputStream servicelog = null;
		String serviceStatus = null;

		try {

			// channel.setOutputStream(System.out,true);

			channel = session.openChannel("shell");
			servicelog = new FileOutputStream("service.log");
			OutputStream inputstream_for_the_channel = channel
					.getOutputStream();
			PrintStream commander = new PrintStream(
					inputstream_for_the_channel, true);

			channel.connect();
			channel.setOutputStream(servicelog, true);
			// channel.setOutputStream(System.out,true);
			switch (status) {
			case STATUS:
				commander.println("cd /opt/mount/apps/ms/init.d");
				commander.println("sudo su");
				commander.println("./tomcat status > op.log");
				do {
					Thread.sleep(10000);
				} while (channel.isEOF());
				System.out.println("Collecting the status .......\n");
				commander.println("exit");
				serviceStatus =  checkStatus();
				break;

			case STOP:
				System.out.println("Trying to stop the service .......\n");
				commander.println("cd /opt/mount/apps/ms/init.d");
				commander.println("sudo su");
				commander.println("./tomcat stop");
				Thread.sleep(10000);
				/*
				 * do { Thread.sleep(5000); } while(channel.isEOF());
				 */
				commander.println("./tomcat status > op.log");
				Thread.sleep(5000);
				System.out.println("Collecting the status .......\n");
				commander.println("exit");
				serviceStatus =  checkStatus();
				break;

			case START:
				System.out.println("Trying to start the service .......\n");
				commander.println("cd /opt/mount/apps/ms/init.d");
				commander.println("sudo su");
				commander.println("./tomcat start");

				Thread.sleep(10000);
				/*
				 * do { Thread.sleep(5000); } while(channel.isEOF());
				 */
				commander.println("./tomcat status > op.log");
				Thread.sleep(5000);
				System.out.println("Collecting the status .......\n");
				commander.println("exit");
				serviceStatus =  checkStatus();
				break;

			default:
				System.out.println("");
				break;
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		channel.disconnect();
		return serviceStatus;
	}

	private static String checkStatus() {

		Channel ftpchannel = null;
		OutputStream output;
		String status = null;

		ChannelSftp ftpc = null;
		try {
			ftpchannel = session.openChannel("sftp");
			output = new FileOutputStream("file1.txt");
			ftpchannel.connect();
			ftpc = (ChannelSftp) ftpchannel;
			ftpc.get("/opt/mount/apps/ms/init.d/op.log", output);
			status = findstatus("file1.txt");
			// System.out.println("service status as of now : " + status);
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ftpc.disconnect();
		ftpchannel.disconnect();
		// System.out.println("service status as of now : " + status);
		return status;
	}

	private static String findstatus(String filepath) {
		BufferedReader br;
		String status1 = "running...";
		String status2 = "Starting";
		String status3 = "stopped";
		String status = null;
		String line = "";

		try {
			br = new BufferedReader(new FileReader(filepath));
			try {
				while ((line = br.readLine()) != null) {

					if ((line.indexOf(status1)) != -1) {
						// System.out.println(status1 +" found");
						status = status1;
					} else if ((line.indexOf(status2)) != -1) {
						// System.out.println(status2 +" found");
						status = status2;
					} else if ((line.indexOf(status3)) != -1) {
						// System.out.println(status3 +" found");
						status = status3;
					} else {
						// System.out.println("None of the status found");
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
		return status;
	}


}
