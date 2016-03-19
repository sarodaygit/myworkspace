package com.hp.de.automation.onramp;

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
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

//import com.hp.de.examples.ExcelOperations;

public class Jobsubmitter {
	public static String printHref = null;


	public String postPrint(String onrampUrl, String oauth, String printerName, String testPath, String trustStore, String requestXml)
	throws HttpException, IOException, InterruptedException, JDOMException
	{
		//		System.out.println(testPath + " " + trustStore + " " + requestXml);

		System.setProperty("javax.net.ssl.trustStore", trustStore);

		//		System.out.println("Value is = " + oauth);

		HttpClient httpclient = new HttpClient();
		httpclient.getHostConfiguration().setProxy("rio.india.hp.com", 8080);

		String pj_url = onrampUrl + "/jobs/printjobs/";

		PostMethod pushJobMethod = new PostMethod(pj_url);

		pushJobMethod.addRequestHeader("Authorization", oauth);

		pushJobMethod.setRequestEntity(new StringRequestEntity(requestXml));
		int pushres = httpclient.executeMethod(pushJobMethod);
		System.out.println("Job created = " + pushres);
		//		System.out.println("Response xml is: " + pushJobMethod.getResponseBodyAsString());
		String inputLine = pushJobMethod.getResponseBodyAsString();

		SAXBuilder builder = new SAXBuilder();
		Reader reader = new StringReader(inputLine);

		Namespace ns1 = Namespace.getNamespace("http://www.hp.com/schemas/imaging/con/cloud/onramp/2009/12/20");

		Document doc = null;

		String datasinkURI = "";
		String closejobURI = null;
		try
		{
			doc = builder.build(reader);
			Element root = doc.getRootElement();

			Element Child = root.getChild("PrintJobDocuments", ns1);
			Element Child2 = Child.getChild("PrintJobDocument", ns1);
			Element Child3 = Child2.getChild("PrintJobDocumentDescription", ns1);
			Element Child4 = Child3.getChild("DataSinkURI", ns1);
			Element Child5 = root.getChild("PrintURI", ns1);

			datasinkURI = Child4.getText();
			closejobURI = Child5.getText();

			/*	System.out.println("DatasinkURI=" + datasinkURI);
			System.out.println("closejobURI=" + onrampUrl + closejobURI);*/

			printHref = closejobURI;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		String filePut = datasinkURI;
		RequestEntity req = new FileRequestEntity(new File(testPath), testPath.substring(testPath.lastIndexOf(".") + 1, testPath.length()));
		PutMethod put = new PutMethod(filePut);

		put.addRequestHeader("Authorization", oauth);
		put.setRequestEntity(req);
		int result = httpclient.executeMethod(put);
		int putcode = put.getStatusCode();
		byte[] resbody = put.getResponseBody();

		//		System.out.println("The response code for PUT of pushing document is:");
		//		System.out.println(resbody.toString());
		System.out.println("Physical file uploaded Response Code =  " + putcode);
		String jobid = getjobid(datasinkURI);
		return jobid;
	}

	public String pullPrint(String onrampUrl, String oauth, String printerName, String testPath, String trustStore, String requestXml)
	throws HttpException, IOException, InterruptedException
	{
		System.setProperty("javax.net.ssl.trustStore", trustStore);

		String realvalue = oauth;
		String XmlRequest = requestXml;

		HttpClient httpclient = new HttpClient();
		httpclient.getHostConfiguration().setProxy("rio.india.hp.com", 8080);

		PostMethod pullJobMethod = new PostMethod(onrampUrl + "/jobs/printjobs/");

		pullJobMethod.addRequestHeader("Authorization", realvalue);
		pullJobMethod.setRequestEntity(new StringRequestEntity(XmlRequest));

		System.out.println("Response code of POST for Print Job is:");
		System.out.println(httpclient.executeMethod(pullJobMethod));

		String inputLine = pullJobMethod.getResponseBodyAsString();

		SAXBuilder builder = new SAXBuilder();
		Reader reader = new StringReader(inputLine);

		Namespace ns1 = Namespace.getNamespace("http://www.hp.com/schemas/imaging/con/cloud/onramp/2009/12/20");

		Document doc = null;

		String datasinkURI = "";
		String closejobURI = null;
		try
		{
			doc = builder.build(reader);
			Element root = doc.getRootElement();
			System.out.println(root);

			Element Child = root.getChild("PrintJobDocuments", ns1);
			Element Child2 = Child.getChild("PrintJobDocument", ns1);
			Element Child3 = Child2.getChild("PrintJobDocumentDescription", ns1);
			Element Child4 = Child3.getChild("DataSinkURI", ns1);
			Element Child5 = root.getChild("PrintURI", ns1);

			datasinkURI = Child4.getText();
			closejobURI = Child5.getText();

			System.out.println("Printing URL :- \n" + testPath);
			printHref = closejobURI;


		}
		catch (Exception e) {
			e.printStackTrace();
		}
		String jobid = getjobid(datasinkURI);
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

		//		String jobid = string.substring(length1, length1 + 39);
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
		//		String docid = string.substring(length2, length2 + 39);

		return jobid;
	}

	public static String getjobstatus(String onrampUrl, String oauth, String jobid, String trustStore)
	throws HttpException, IOException, InterruptedException, JDOMException
	{

		System.out.println("trying to get status of the job within 2 minutes................");
		Thread.sleep(30000);
		System.setProperty("javax.net.ssl.trustStore", trustStore);
		HttpClient httpclient = new HttpClient();
		httpclient.getHostConfiguration().setProxy("rio.india.hp.com", 8080);
		String job_url = onrampUrl + "/jobs/printjobs/"+jobid+"/status/";
		GetMethod getstatusMethod = new GetMethod(job_url);
		getstatusMethod.addRequestHeader("Authorization", oauth);
		int rescode = httpclient.executeMethod(getstatusMethod);
		//		System.out.println("Response code for 1st POST is: " + rescode);
		String jobstatus = null;
		if(rescode == 200) {
			String inputLine = getstatusMethod.getResponseBodyAsString();
			SAXBuilder builder = new SAXBuilder();
			Reader reader = new StringReader(inputLine);
			Namespace ns1 = Namespace.getNamespace("http://www.hp.com/schemas/imaging/con/cloud/onramp/2009/12/20");
			Document doc = null;
			try
			{
				doc = builder.build(reader);
				Element root = doc.getRootElement();
				Element Child = root.getChild("JobState", ns1);
				jobstatus = Child.getText();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		} else {
			System.out.println("Job may not be created sucessfully");
		}

		return jobstatus;
	}

	public void executor(String p_stackdetails, String p_datafile, String pstorefile) throws IOException,
	FileNotFoundException, HttpException, InterruptedException,
	JDOMException {


		ExcelOperations exc = new ExcelOperations();
		Properties server_prop = new Properties();
		OAuthGen ogen = new OAuthGen(p_stackdetails);
		server_prop.load(new FileInputStream(p_stackdetails));


		String testType = null, testName = null, testDescription = null, testPath = null, fileName = null, Doc_Type = null, requestXml = null, printerName = null,
		oauth = null, onrampUrl = null, trustStore = null, status = null, authtype=null, useremail = null;

		List sheetdata = exc.readexceldata(p_datafile, Integer.valueOf(0));
		onrampUrl = "https://" + server_prop.getProperty("ONRAMP_IP") + "/onramp";
		trustStore = server_prop.getProperty("TRUSTSTORE");
		String jobid =null, res = null;
		for (int i = 1; i < sheetdata.size(); i++)
		{
			res = null;
			List testcase_row = (List)sheetdata.get(i);

			testType = String.valueOf(testcase_row.get(0));
			testName = String.valueOf(testcase_row.get(2));
			requestXml = String.valueOf(testcase_row.get(4));
			testPath = String.valueOf(testcase_row.get(5));
			printerName = String.valueOf(testcase_row.get(8));
			authtype = String.valueOf(testcase_row.get(10));
			useremail = String.valueOf(testcase_row.get(11));


			oauth = ogen.getAuth(p_stackdetails,pstorefile,printerName,authtype,useremail);

			//			System.out.println(printerName + "  " + oauth);
			int j = 3;
			
			if (testType.equalsIgnoreCase("pull")) {
				System.out.println("\nExecuting Test :- " + testName + "\n");
				jobid= pullPrint(onrampUrl, oauth, printerName, testPath, trustStore, requestXml);
				System.out.println("Job is created sucessfully and job id is " + jobid);
				do {
					res = getjobstatus(onrampUrl,oauth,jobid,trustStore);
					System.out.println("Status of the " + jobid + " = " + res );
					if(res.equalsIgnoreCase("Completed") || res.equalsIgnoreCase("Aborted")|| res.equalsIgnoreCase("Cancelled")){
						j =0;
					}
					j--;
				}while(j > 0);
//				} while(! res.equalsIgnoreCase("Completed") || res.equalsIgnoreCase("Aborted")|| res.equalsIgnoreCase("Cancelled"));
				

			}
			else if (testType.equalsIgnoreCase("push")) {
				System.out.println("\nExecuting Test :- " + testName + "\n");
				jobid = postPrint(onrampUrl, oauth, printerName, testPath, trustStore, requestXml);
				System.out.println("Job is created sucessfully and job id is " + jobid);
				
				do {
					res = getjobstatus(onrampUrl,oauth,jobid,trustStore);
					System.out.println("Status of the " + jobid + " = " + res );
					if(res.equalsIgnoreCase("Completed") || res.equalsIgnoreCase("Aborted")|| res.equalsIgnoreCase("Cancelled")){
						j =0;
					}
					j--;
				}while(j > 0);
//				} while(! res.equalsIgnoreCase("Completed") || res.equalsIgnoreCase("Aborted")|| res.equalsIgnoreCase("Cancelled"));
				


			} else {
				System.out.println("Done Testing");
			}
		}
		//		threading();

	}




}
