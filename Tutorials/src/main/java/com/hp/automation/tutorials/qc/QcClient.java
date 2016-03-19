package com.hp.automation.tutorials.qc;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;



import com.noelios.restlet.util.Base64;

//import org.apache.commons.codec.binary.Base64;
/*import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;*/

public class QcClient {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String clientId = getSessionID();
//		String strrr = getSessionID1();
		
	}

	private static String getSessionID() throws IOException {
		// TODO Auto-generated method stub
		HttpClient httpClient = new HttpClient();
//		httpClient.getHostConfiguration().setProxy("web-proxy.cup.hp.com", 8080);
		GetMethod getmethod = new GetMethod("http://qc1f.austin.hp.com/qcbin/authentication-point/authenticate");
//		GetMethod getmethod = new GetMethod("http://qc1d.atlanta.hp.com/qcbin/rest/domains/IPG/projects/cloudservice/tests");
       
           getmethod.setRequestHeader("Authorization","Basic "
						+ Base64.encode(
								new StringBuilder(String
										.valueOf("_svc_cloudserviceQC1b")).append(":")
										.append("Aut0mat!on123").toString()
										.getBytes(), false));
        
        
        int getres = httpClient.executeMethod(getmethod);
        String response= getmethod.getResponseBodyAsString();
         
        
        
//        System.out.println(getmethod.getResponseHeaders().toString());
//        System.out.println(getmethod.getStatusLine().toString());
//       getmethod.getr
        String output;
        System.out.println("Output from Server .... \n"+ getres + "\n" + getmethod.getResponseHeader("Set-Cookie")); 
        
        
		return null;
	}

	
	
}
