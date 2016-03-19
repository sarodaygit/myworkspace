package com.hp.automation.commons;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.hp.automation.ha.appconfig.AppConfiguration;

public class PrinterRegisterService {
	private AppConfiguration  appconfig;

	public PrinterRegisterService(AppConfiguration appconfig) {
		this.appconfig= appconfig;
		
	}

	public String registerPrinter(String p_stack_name, String p_lang_code,
			String p_country_code, String prn_reg_url) {

		String responsebody = null;
		int response = 0;

		PostMethod prnJobMethod = new PostMethod(prn_reg_url);

		prnJobMethod.addRequestHeader("Host", "16.177.36.108");
		prnJobMethod.addRequestHeader("Content-Type", "application/xml");
		// prnJobMethod.addRequestHeader("Content-Length", "1073");

		String requestbody = "<?xml version='1.0' encoding='UTF-8'?>"
				+ "<registerPrinter>" + "<stack>"
				+ p_stack_name
				+ "</stack>"
				+ "<modelName>HP PhotoSmart D110a</modelName>"
				+ "<modelNumber>CE861A:HP LaserJet Thunderbird Series</modelNumber>"
				+ "<duration>120</duration>"
				+ "<breathtime>-1</breathtime>"
				+ "<waitForInstructionPage>true</waitForInstructionPage>"
				+ "<registerTime>4200000</registerTime>"
				+ "<country>"
				+ p_country_code
				+ "</country>"
				+ "<language>"
				+ p_lang_code
				+ "</language>"
				+ "<owner>FLEET</owner>"
				+ "<ownerObjective>SetYourObjective</ownerObjective>"
				+ "<prefetchAuthCode>false</prefetchAuthCode>"
				+ "<number>1</number>"
				+ "<registerbatch>1</registerbatch>"
				+ "<delaynextbatch>5.0</delaynextbatch>"
				+ "<test>none</test>"
				+ "<numjobs>1</numjobs>"
				+ "<jobFiles>none</jobFiles>"
				+ "<delaynextjob>60.0</delaynextjob>"
				+ "<hpcUser></hpcUser>"
				+ "<batchsizeperprinter>1</batchsizeperprinter>"
				+ "<delaybetweentowjobsinbatchperprinter>0.0</delaybetweentowjobsinbatchperprinter>"
				+ "<submitInSequence>true</submitInSequence>"
				+ "<initialDelay>0.0</initialDelay>"
				+ "<allowsSips>true</allowsSips>"
				+ "<allowsMobileApps>true</allowsMobileApps>"
				+ "<allowsEmail>true</allowsEmail>"
				+ "<protectionMode>false</protectionMode>"
				+ "<allowsUsageDataCollection>false</allowsUsageDataCollection>"
				+ "</registerPrinter>";
		prnJobMethod.setRequestBody(requestbody);

		// System.out.println("Printer registration URL = " + prn_reg_url);
		// System.out.println("\nRequestBody = " + requestbody + "\n");

		// Create Httpclient
		HttpClient httpclient = new HttpClient();

		try {

			response = httpclient.executeMethod(prnJobMethod);
			System.out.println("Http response code = " + response);

			if (response == 200 || response == 201 || response == 202) {
				responsebody = prnJobMethod.getResponseBodyAsString();
				// System.out.println("\nHttp ResponseBody = "+responsebody);

			} else {

				System.out.println("Printer registration request failed");
			}

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responsebody;
	}

	public String getInforSheetURL(String responsebody) {
		SAXBuilder builder = new SAXBuilder();
		Reader reader;
		Document doc;
		String infosheeturl = null;
		reader = new StringReader(responsebody);
		try {
			doc = builder.build(reader);
			Element root = doc.getRootElement();
			Element prn_element = root.getChild("printer");

			infosheeturl = prn_element.getChildText("registrationPage");
			System.out.println("PrinterEmailAddress = "
					+ prn_element.getChildText("printerEmailId"));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println("Infosheet URL = "+infosheeturl);
		return infosheeturl;
	}

	public String registerPrinter() {
		String stackName = appconfig.getStackName();
		String printerSimulatorHost = appconfig.getprinterSimulatorHost();
		String responsebody = null;
		int response = 0;

		PostMethod prnJobMethod = new PostMethod(printerSimulatorHost);

		prnJobMethod.addRequestHeader("Host", "16.177.36.108");
		prnJobMethod.addRequestHeader("Content-Type", "application/xml");
		// prnJobMethod.addRequestHeader("Content-Length", "1073");

		
		String requestbody = "<?xml version='1.0' encoding='UTF-8'?>"
				+ "<registerPrinter>" + "<stack>" + stackName + "</stack>"
				+ "<modelName>HP PhotoSmart D110a</modelName>"
				+ "<modelNumber>CE861A:HP LaserJet Thunderbird Series</modelNumber>"
				+ "<duration>120</duration>"
				+ "<breathtime>-1</breathtime>"
				+ "<waitForInstructionPage>true</waitForInstructionPage>"
				+ "<registerTime>4200000</registerTime>"
				+ "<country>unitedStates</country>"
				+ "<language>en</language>"
				+ "<owner>FLEET</owner>"
				+ "<ownerObjective>SetYourObjective</ownerObjective>"
				+ "<prefetchAuthCode>false</prefetchAuthCode>"
				+ "<number>1</number>"
				+ "<registerbatch>1</registerbatch>"
				+ "<delaynextbatch>5.0</delaynextbatch>"
				+ "<test>none</test>"
				+ "<numjobs>1</numjobs>"
				+ "<jobFiles>none</jobFiles>"
				+ "<delaynextjob>60.0</delaynextjob>"
				+ "<hpcUser></hpcUser>"
				+ "<batchsizeperprinter>1</batchsizeperprinter>"
				+ "<delaybetweentowjobsinbatchperprinter>0.0</delaybetweentowjobsinbatchperprinter>"
				+ "<submitInSequence>true</submitInSequence>"
				+ "<initialDelay>0.0</initialDelay>"
				+ "<allowsSips>true</allowsSips>"
				+ "<allowsMobileApps>true</allowsMobileApps>"
				+ "<allowsEmail>true</allowsEmail>"
				+ "<protectionMode>false</protectionMode>"
				+ "<allowsUsageDataCollection>false</allowsUsageDataCollection>"
				+ "</registerPrinter>";
		prnJobMethod.setRequestBody(requestbody);

		// System.out.println("Printer registration URL = " + prn_reg_url);
		// System.out.println("\nRequestBody = " + requestbody + "\n");

		// Create Httpclient
		HttpClient httpclient = new HttpClient();

		try {

			response = httpclient.executeMethod(prnJobMethod);
			System.out.println("Http response code = " + response);

			if (response == 200 || response == 201 || response == 202) {
				responsebody = prnJobMethod.getResponseBodyAsString();
				// System.out.println("\nHttp ResponseBody = "+responsebody);

			} else {

				System.out.println("Printer registration request failed");
			}

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responsebody;
		
		

	}

}
