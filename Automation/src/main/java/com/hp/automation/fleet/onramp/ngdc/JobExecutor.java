package com.hp.automation.fleet.onramp.ngdc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

//import com.hp.de.examples.ExcelOperations;

public class JobExecutor implements Runnable {

	static Logger log = Logger.getLogger(JobExecutor.class.getName());
	String printHref = null;
	String onrampUrl;
	String oauth, printerName, testPath;
	String trustStore;
	String stackdetails;
	String requestXml, testType, testName, authtype, useremail, datafile,
			pstorefile;
	String PROXY_HOST;
	Integer PROXY_PORT;
	Properties server_prop = new Properties();
	PrintWriter out;

	

	public JobExecutor(String sDetails, String dFile, String pFile, List tRow,
			String auth) throws FileNotFoundException,
			IOException {
		stackdetails = sDetails;
		datafile = dFile;
		pstorefile = pFile;
		server_prop.load(new FileInputStream(stackdetails));
		onrampUrl = "https://" + server_prop.getProperty("ONRAMP_IP")
				+ "/onramp";
		trustStore = server_prop.getProperty("TRUSTSTORE");
		testType = String.valueOf(tRow.get(0));
		testName = String.valueOf(tRow.get(2));
		requestXml = String.valueOf(tRow.get(4));
		testPath = String.valueOf(tRow.get(5));
		printerName = String.valueOf(tRow.get(8));
		authtype = String.valueOf(tRow.get(10));
		useremail = String.valueOf(tRow.get(11));
		PROXY_HOST = server_prop.getProperty("PROXY_HOST");
		
		PROXY_PORT = Integer.valueOf(Integer.parseInt(server_prop
				.getProperty("PROXY_PORT")));
		
				oauth = auth;
		
		
	}

	public String postPrint(String oauth, String printerName, String testPath,
			String requestXml, PrintWriter out)  {
		
		String jobid=null;
		 log.debug(testPath + " " + trustStore + " " + requestXml);

		System.setProperty("javax.net.ssl.trustStore", trustStore);

		log.debug("Value is = " + oauth);

		HttpClient httpclient = new HttpClient();
		// httpclient.getHostConfiguration().setProxy("web-proxy.cup.hp.com",
		// PROXY_PORT);
		httpclient.getHostConfiguration().setProxy(PROXY_HOST, PROXY_PORT);
		String pj_url = onrampUrl + "/jobs/printjobs/";

		PostMethod pushJobMethod = new PostMethod(pj_url);

		pushJobMethod.addRequestHeader("Authorization", oauth);
		
		pushJobMethod.setRequestEntity(new StringRequestEntity(requestXml));
		int pushres = 0;
		try {
			pushres = httpclient.executeMethod(pushJobMethod);
			
			System.out.println("Posting the job for " + testName + " is completed = " + pushres);
			log.info("Posted the job for " + testName + " = " 				+ pushres);
			/*System.out.println("Response xml is: "
					+ pushJobMethod.getResponseBodyAsString());*/
			String inputLine;
			inputLine = pushJobMethod.getResponseBodyAsString();
			log.debug(printerName + " = " + inputLine);
			SAXBuilder builder = new SAXBuilder();
			Reader reader = new StringReader(inputLine);
			Namespace ns1 = Namespace
					.getNamespace("http://www.hp.com/schemas/imaging/con/cloud/onramp/2009/12/20");
			String datasinkURI = getDataSinkUri(builder, reader, ns1);
			jobid = getjobid(datasinkURI);
			log.debug(testName+" : "+printerName +" - " +"Job Id"+" = " + jobid);
			log.debug(testName+" : "+printerName +" - " +"Document Id"+" = "+ getdocid(datasinkURI));
			String filePut = datasinkURI;
			RequestEntity req = new FileRequestEntity(new File(testPath),
					testPath.substring(testPath.lastIndexOf(".") + 1,
							testPath.length()));
			PutMethod put = new PutMethod(filePut);
			put.addRequestHeader("Authorization", oauth);
			put.setRequestEntity(req);
			log.info("Uploading the document for the Job");
			int result = httpclient.executeMethod(put);
			int putcode = put.getStatusCode();
			byte[] resbody = put.getResponseBody();
			log.info("File uploaded for " + testName + " = " + putcode);
			log.info("Job id of " + testName + " is " + jobid);
			
			
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return jobid;
	}

	
	private String getDataSinkUri(SAXBuilder builder, Reader reader,
			Namespace ns1) {
		Document doc;
		String datasinkURI = "";
		String closejobURI = null;
		try {
			doc = builder.build(reader);
			Element root = doc.getRootElement();

			Element Child = root.getChild("PrintJobDocuments", ns1);
			Element Child2 = Child.getChild("PrintJobDocument", ns1);
			Element Child3 = Child2
					.getChild("PrintJobDocumentDescription", ns1);
			Element Child4 = Child3.getChild("DataSinkURI", ns1);
			Element Child5 = root.getChild("PrintURI", ns1);

			datasinkURI = Child4.getText();
			closejobURI = Child5.getText();

			/*
			 * System.out.println("DatasinkURI=" + datasinkURI);
			 * System.out.println("closejobURI=" + onrampUrl + closejobURI);
			 */

			printHref = closejobURI;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datasinkURI;
	}

	public String pullPrint(String oauth, String printerName, String testPath,
			String requestXml, PrintWriter out) throws HttpException,
			IOException, InterruptedException, JDOMException {

		 log.debug(testPath + " " + trustStore + " " + requestXml);
		String res = null;
		System.setProperty("javax.net.ssl.trustStore", trustStore);
		log.debug("Value is = " + oauth);
		String realvalue = oauth;
		String XmlRequest = requestXml;

		HttpClient httpclient = new HttpClient();
		httpclient.getHostConfiguration().setProxy(PROXY_HOST, PROXY_PORT);

		PostMethod pullJobMethod = new PostMethod(onrampUrl
				+ "/jobs/printjobs/");

		pullJobMethod.addRequestHeader("Authorization", realvalue);
		pullJobMethod.setRequestEntity(new StringRequestEntity(XmlRequest));
		int pullres = httpclient.executeMethod(pullJobMethod);
		log.info("Posted the job for " + testName + " = "
				+ pullres);

		String inputLine = pullJobMethod.getResponseBodyAsString();
		log.debug(printerName +"Pull response Code = "+ " = " + inputLine);

		SAXBuilder builder = new SAXBuilder();
		Reader reader = new StringReader(inputLine);

		Namespace ns1 = Namespace
				.getNamespace("http://www.hp.com/schemas/imaging/con/cloud/onramp/2009/12/20");

		Document doc = null;

		String datasinkURI = getDataSinkUri(builder, reader, ns1);
		// String closejobURI = null;

		String jobid = getjobid(datasinkURI);
		System.out.println("Job id of " + testName + " is " + jobid);
		log.info("Job id of " + testName + " is " + jobid);
		log.debug(printerName + " = " + jobid);
		log.debug(printerName + " = " + getdocid(datasinkURI));

		return jobid;
	}

	private String getdocid(String datasinkURI) {
		String string = datasinkURI;
		String staticstring1 = "onramp/jobs/printjobs/";
		String staticstring2 = "/documents/";
		int index1 = string.indexOf(staticstring1);
		int length1 = index1 + staticstring1.length();
		int index2 = string.indexOf(staticstring2);
		int length2 = index2 + staticstring2.length();

		// String jobid = string.substring(length1, length1 + 39);
		String docid = string.substring(length2, length2 + 39);

		return docid;
	}

	private String getjobid(String datasinkURI) {
		String string = datasinkURI;
		String staticstring1 = "onramp/jobs/printjobs/";
		String staticstring2 = "/documents/";
		int index1 = string.indexOf(staticstring1);
		int length1 = index1 + staticstring1.length();
		int index2 = string.indexOf(staticstring2);
		int length2 = index2 + staticstring2.length();

		String jobid = string.substring(length1, length1 + 39);
		// String docid = string.substring(length2, length2 + 39);

		return jobid;
	}

	public String getjobstatus(String oauth, String jobid)
			throws HttpException, IOException, InterruptedException,
			JDOMException {
		
		Thread.sleep(45000);
		System.setProperty("javax.net.ssl.trustStore", trustStore);
		HttpClient httpclient = new HttpClient();
		httpclient.getHostConfiguration().setProxy(PROXY_HOST, PROXY_PORT);
		String job_url = onrampUrl + "/jobs/printjobs/" + jobid + "/status/";
		GetMethod getstatusMethod = new GetMethod(job_url);
		getstatusMethod.addRequestHeader("Authorization", oauth);
		int rescode = httpclient.executeMethod(getstatusMethod);
		// System.out.println("Response code for 1st POST is: " + rescode);
		String jobstatus = null;
		if (rescode == 200) {
			String inputLine = getstatusMethod.getResponseBodyAsString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = new StringReader(inputLine);
			Namespace ns1 = Namespace
					.getNamespace("http://www.hp.com/schemas/imaging/con/cloud/onramp/2009/12/20");
			Document doc = null;
			try {
				doc = builder.build(reader);
				Element root = doc.getRootElement();
				Element Child = root.getChild("JobState", ns1);
				jobstatus = Child.getText();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			
			log.error("Job may not be created sucessfully");
		}

		return jobstatus;
	}

	public String JobResultAssertion(String jobStatus) {

		String result = null;
		if (jobStatus.equalsIgnoreCase("Completed")) {
			result = "Passed";
		} else {
			result = "Failed";
		}

		return result;

	}

	// **************RUN METHOD********************************************
	// @Override
	public void run() {

		String jstatus = null, jobid = null, jobResult = null;

		int j = 3;

		if (testType.equalsIgnoreCase("pull")) {
			log.info("\nExecuting Test :- " + testName + " on " + printerName
					+ "\n");
			
			try {
				jobid = pullPrint(oauth, printerName, testPath, requestXml, out);
				log.info(testName + " : " + printerName
						+ " Getting latest status of the Job = " + jobid);
				do {
					jstatus = getjobstatus(oauth, jobid);
					log.info("Status of the job " + testName + ":"
							+ printerName + " and job id ->" + jobid + " = "
							+ jstatus);
					
					if (jstatus.equalsIgnoreCase("Completed")
							|| jstatus.equalsIgnoreCase("Aborted")
							|| jstatus.equalsIgnoreCase("Cancelled")) {
						j = 0;
					}
					j--;
				} while (j > 0);

			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (testType.equalsIgnoreCase("push")) {
			log.info("Executing Test :- \"" + testName + "\" on " + printerName);
			
			try {
				jobid = postPrint(oauth, printerName, testPath, requestXml, out);
				log.info(testName + " : " + printerName
						+ " Getting latest status of the Job = " + jobid);
				do {
					jstatus = getjobstatus(oauth, jobid);
					log.info("Status of the job " + testName + ":"
							+ printerName + " and job id ->" + jobid + " = "
							+ jstatus);
					
					if (jstatus.equalsIgnoreCase("Completed")
							|| jstatus.equalsIgnoreCase("Aborted")
							|| jstatus.equalsIgnoreCase("Cancelled")) {
						j = 0;
					}
					j--;
				} while (j > 0);

			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				log.info("XML ISSSSSSUE");
			}

			jobResult = JobResultAssertion(jstatus);
			log.info("\n\n*************************************************************************\n");
			log.info("Test Name = " + testName + "\nPrinter Name = "
					+ printerName + "\nJob id = " + jobid + "\nJob Status = "
					+ jstatus + "\n Result = " + jobResult + "\n");
			
			log.info("\n\n*************************************************************************\n\n");
			
		} else {
			log.info("Done Testing");
			
		}

	}

}
