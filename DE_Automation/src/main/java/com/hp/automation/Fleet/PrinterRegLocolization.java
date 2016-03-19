package com.hp.automation.Fleet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class PrinterRegLocolization {
	static String prn_reg_url = "http://16.177.36.108/PSRestUtils/v/1.0/printer/register";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String infosheeturl = null, downloadpath = null, response = null;
		String lang_code = "en";
		String cty_code = "unitedStates";
		
		response = registerPrinter(lang_code,cty_code);
//		System.out.println("\nHttp ResponseBody = "+response);
		infosheeturl = getinfosheeturl(response);
		System.out.println("Infosheet download path = " + infosheeturl);
	    downloadpath = "C:/Temp/" + lang_code+".pcl3gui";
    	int download_status = downloadFileData(infosheeturl, downloadpath);
		System.out.println("File download status = " + download_status);

	}

	

	private static int downloadFileData(String fileDataURL, String localDownloadPath) {
		// TODO Auto-generated method stub
		// makes the HTTP get request
        HttpClient client = new HttpClient();
        InputStream in;
        FileOutputStream out;
		
        
        GetMethod get = new GetMethod(fileDataURL);
       
        try {
			client.executeMethod(get);
			 in = get.getResponseBodyAsStream();
			 out = new FileOutputStream(new File(localDownloadPath));   
		        byte[] b = new byte[1024];
		        int len = 0;
		        while ((len = in.read(b)) != -1) {
		            out.write(b, 0, len);
		        }
		        in.close();
		        out.close();
			
			
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       

        // get HTTP response
        Integer statusCode = get.getStatusCode();
        Header[] headers = get.getResponseHeaders();

        return statusCode;
		
	}



	private static String registerPrinter(String p_lang_code,String p_country_code) {
		
		String responsebody = null;
		int response=0;
		
	
		
		PostMethod prnJobMethod = new PostMethod(prn_reg_url);

		prnJobMethod.addRequestHeader("Host", "16.177.36.108");
		prnJobMethod.addRequestHeader("Content-Type", "application/xml");
//		prnJobMethod.addRequestHeader("Content-Length", "1073");
	
		
		String requestbody = "<?xml version='1.0' encoding='UTF-8'?>"+
		"<registerPrinter>"+
		"<stack>pie1</stack>"+
		"<modelName>HP PhotoSmart D110a</modelName>"+
		"<modelNumber>CN731A</modelNumber>"+
		"<duration>120</duration>"+
		"<breathtime>-1</breathtime>"+
		"<waitForInstructionPage>true</waitForInstructionPage>"+
		"<registerTime>4200000</registerTime>"+
		"<country>"+p_country_code+"</country>"+
		"<language>"+p_lang_code+"</language>"+
		"<owner>VIJAYBHANU</owner>"+
		"<ownerObjective>SetYourObjective</ownerObjective>"+
		"<prefetchAuthCode>false</prefetchAuthCode>"+
		"<number>1</number>"+
		"<registerbatch>1</registerbatch>"+
		"<delaynextbatch>5.0</delaynextbatch>"+
		"<test>none</test>"+
		"<numjobs>1</numjobs>"+
		"<jobFiles>none</jobFiles>"+
		"<delaynextjob>60.0</delaynextjob>"+
		"<hpcUser></hpcUser>"+
		"<batchsizeperprinter>1</batchsizeperprinter>"+
		"<delaybetweentowjobsinbatchperprinter>0.0</delaybetweentowjobsinbatchperprinter>"+
		"<submitInSequence>true</submitInSequence>"+
		"<initialDelay>0.0</initialDelay>"+
		"<allowsSips>true</allowsSips>"+
		"<allowsMobileApps>true</allowsMobileApps>"+
		"<allowsEmail>true</allowsEmail>"+
		"<protectionMode>false</protectionMode>"+
		"<allowsUsageDataCollection>false</allowsUsageDataCollection>"+
		"</registerPrinter>";
		prnJobMethod.setRequestBody(requestbody);
		
		System.out.println("Printer registration URL = " + prn_reg_url);
		System.out.println("\nRequestBody = " + requestbody + "\n");
		
		// Create Httpclient
		HttpClient httpclient = new HttpClient();

		try {
			
			response = httpclient.executeMethod(prnJobMethod);
			System.out.println("Http response code = "+response);
			
			
			if(response == 200 || response == 201 || response == 202){
				responsebody = prnJobMethod.getResponseBodyAsString();
//				System.out.println("\nHttp ResponseBody = "+responsebody);
				
				
			
				
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

	private static String getinfosheeturl(String responsebody)
			 {
		SAXBuilder builder = new SAXBuilder();
		Reader reader;
		Document doc;
		String infosheeturl =null;
		reader = new StringReader(responsebody);
		try {
			doc = builder.build(reader);
			Element root = doc.getRootElement();
			Element prn_element = root.getChild("printer");
			
			infosheeturl = prn_element.getChildText("registrationPage");
			
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("Infosheet URL = "+infosheeturl);
		return infosheeturl;
	}
	
	
	
	
	
	

}
