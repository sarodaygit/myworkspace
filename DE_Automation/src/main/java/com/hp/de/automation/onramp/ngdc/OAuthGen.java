package com.hp.de.automation.onramp.ngdc;


import com.jcraft.jsch.JSchException;
import com.noelios.restlet.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.security.oauth.common.OAuthCodec;

public class OAuthGen
{
  private static final String NULL = null;
  String OAUTH_CONSUMER_KEY = "oauth_consumer_key=";
  String OAUTH_SIGNATURE_METHOD = "&oauth_signature_method=PLAINTEXT";
  String OAUTH_CALLBACK = "oauth_callback=oob";
  String OAUTH_SIGNATURE = "oauth_signature=";
  String TRUSTSTORE_STR = "javax.net.ssl.trustStore";
  String AMPERSAND = "&";
  String COMMA = ",";
  String ONRAMP_IP;
  String OFFRAMP_IP;
  String SIP_CONSUMER_KEY;
  String SIP_CONSUMER_SIGNATURE;
  String GGL_CONSUMER_KEY;
  String GGL_CONSUMER_SIGNATURE;
  String MOB_CONSUMER_KEY;
  String MOB_CONSUMER_SIGNATURE;
  String PRINTER_ID;
  String PRINTER_KEY;
  String PRINTER_EMAIL_ADDRESS;
  String USER_EMAIL;
  String OWNER_ID;
  String EPC_HOST;
  String EPC_PORT;
  String TRUSTSTORE;
  String KEYSTORE;
  String PROXY_HOST;
  Integer PROXY_PORT;
  String setup_file_path;
  String shardurl;
//  setup_details sd_obj = new setup_details();
  Properties server_prop = new Properties();
  Properties printer_details = new Properties();
  DBConnector dbc;
  

  public OAuthGen(String spropFileName)
    throws FileNotFoundException, IOException
  {
    server_prop.load(new FileInputStream(spropFileName));
    ONRAMP_IP = this.server_prop.getProperty("ONRAMP_IP");
    OFFRAMP_IP = this.server_prop.getProperty("OFFRAMP_IP");
    SIP_CONSUMER_KEY = this.server_prop.getProperty("SIP_CONSUMER_KEY");
    SIP_CONSUMER_SIGNATURE = this.server_prop.getProperty("SIP_CONSUMER_SIGNATURE");
    MOB_CONSUMER_KEY = this.server_prop.getProperty("MOB_CONSUMER_KEY");
    MOB_CONSUMER_SIGNATURE = this.server_prop.getProperty("MOB_CONSUMER_SIGNATURE");
//    this.USER_EMAIL = this.server_prop.getProperty("USER_EMAIL");
    EPC_HOST = this.server_prop.getProperty("EPC_HOST");
    EPC_PORT = this.server_prop.getProperty("EPC_PORT");
    TRUSTSTORE = this.server_prop.getProperty("TRUSTSTORE");
    KEYSTORE = this.server_prop.getProperty("KEYSTORE");
    PROXY_HOST = this.server_prop.getProperty("PROXY_HOST");
    PROXY_PORT = Integer.valueOf(Integer.parseInt(this.server_prop.getProperty("PROXY_PORT")));
    System.setProperty(this.TRUSTSTORE_STR, this.TRUSTSTORE);
    dbc = new DBConnector(spropFileName);
    
    
    
   }

	public String get_sipoauth(String printerEmailAddress)
			throws HttpException, IOException, JSchException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {

		String prn_temp = dbc.getCredentials(shardurl, printerEmailAddress);

		this.PRINTER_ID = prn_temp.split(",")[0];
		this.PRINTER_KEY = prn_temp.split(",")[1];
		this.PRINTER_EMAIL_ADDRESS = printerEmailAddress;

		System.setProperty(this.TRUSTSTORE_STR, this.TRUSTSTORE);
		String SERVER_URL = "https://" + this.ONRAMP_IP + "/onramp/";
		String REQUEST_URL = SERVER_URL + "tokens/temp?deviceid="
				+ this.PRINTER_ID + "&";
		String AUTHENTICATION_URL = "https://" + this.OFFRAMP_IP + "/offramp/"
				+ "Authorization?";
		String ACCESSTOKEN_URL = SERVER_URL + "tokens/access?";

		HttpClient httpclient = new HttpClient();
		httpclient.getHostConfiguration().setProxy(this.PROXY_HOST,
				this.PROXY_PORT.intValue());
		// System.out.println(REQUEST_URL + this.OAUTH_CONSUMER_KEY +
		// this.SIP_CONSUMER_KEY + this.OAUTH_SIGNATURE_METHOD + this.AMPERSAND
		// + this.OAUTH_SIGNATURE + this.SIP_CONSUMER_SIGNATURE + this.AMPERSAND
		// + this.OAUTH_CALLBACK);

		PostMethod firstMethod = new PostMethod(REQUEST_URL
				+ this.OAUTH_CONSUMER_KEY + this.SIP_CONSUMER_KEY
				+ this.OAUTH_SIGNATURE_METHOD + this.AMPERSAND
				+ this.OAUTH_SIGNATURE + this.SIP_CONSUMER_SIGNATURE
				+ this.AMPERSAND + this.OAUTH_CALLBACK);
		try {
			int firstres = httpclient.executeMethod(firstMethod);
			// System.out.println(firstres);
			// System.out.println(firstMethod.getResponseBodyAsString());
		} catch (HttpException e1) {
			e1.printStackTrace();
			System.out.println("Failed!!");
			System.exit(0);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Failed!!");
			System.exit(0);
		}

		String responseData = "";
		try {
			responseData = firstMethod.getResponseBodyAsString();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed!!");
			System.exit(0);
		}

		String[] requestTokenData = responseData.split("&");

		HttpMethod secondMethod = new GetMethod(AUTHENTICATION_URL
				+ "temp_token=" + requestTokenData[0].split("=")[1]);

		String getvalue = requestTokenData[0].split("=")[1];

		secondMethod.setRequestHeader(
				"Authorization",
				"Basic "
						+ Base64.encode(
								new StringBuilder(String
										.valueOf(this.PRINTER_ID)).append(":")
										.append(this.PRINTER_KEY).toString()
										.getBytes(), false));

		secondMethod.setFollowRedirects(false);

		// int secondres = httpclient.executeMethod(secondMethod);

		String verificationCode = "";

		if (httpclient.executeMethod(secondMethod) == 302) {
			// System.out.println(secondMethod.getResponseHeader("Location").getValue());
			verificationCode = secondMethod.getResponseHeader("Location")
					.getValue().split("\\?")[1].split("&")[1];
		} else {
			try {
				System.out.println(secondMethod.getResponseBodyAsString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Failed!!");
			System.exit(0);
		}

		HttpMethod thirdMethod = new PostMethod(ACCESSTOKEN_URL
				+ this.OAUTH_CONSUMER_KEY + this.SIP_CONSUMER_KEY
				+ this.OAUTH_SIGNATURE_METHOD + this.AMPERSAND
				+ this.OAUTH_CALLBACK + this.AMPERSAND + verificationCode
				+ this.AMPERSAND + requestTokenData[0] + this.AMPERSAND
				+ this.OAUTH_SIGNATURE + this.SIP_CONSUMER_SIGNATURE
				+ OAuthCodec.oauthEncode(requestTokenData[1].split("=")[1]));

		String[] accessToken = (String[]) null;
		try {
			int thirdres = httpclient.executeMethod(thirdMethod);
			accessToken = thirdMethod.getResponseBodyAsString().split("&");
			// System.out.println(accessToken[0] + accessToken[1]);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed!!");
			System.exit(0);
		}
		String mainvalue = this.OAUTH_CONSUMER_KEY + this.SIP_CONSUMER_KEY
				+ this.OAUTH_SIGNATURE_METHOD + this.AMPERSAND + accessToken[0]
				+ this.AMPERSAND + this.OAUTH_SIGNATURE
				+ this.SIP_CONSUMER_SIGNATURE
				+ OAuthCodec.oauthEncode(accessToken[1].split("=")[1]);
		String realvalue = "";
		realvalue = "OAuth " + mainvalue.replace('&', ',');
		return realvalue;
	}

	public String get_moboauth(String useremail) throws JSchException,
			IOException, InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		// java.lang.System.setProperty(TRUSTSTORE_STR,TRUSTSTORE);

		String SERVER_URL = "https://" + ONRAMP_IP + "/onramp/";
		String REQUEST_URL = SERVER_URL + "tokens/temp?";
		String ACCESSTOKEN_URL = SERVER_URL + "tokens/access?";
		String verificationCode = null;

		HttpClient httpclient = new HttpClient();
		if (System.getProperty("proxy", "true").equals("true")) {
			// httpclient.getHostConfiguration().setProxy("web-proxy.rose.hp.com",
			// 8088);
			httpclient.getHostConfiguration().setProxy(PROXY_HOST, PROXY_PORT);
		}

		// For getting the request token
		String url = REQUEST_URL + OAUTH_CONSUMER_KEY + MOB_CONSUMER_KEY
				+ OAUTH_SIGNATURE_METHOD + AMPERSAND + OAUTH_SIGNATURE
				+ MOB_CONSUMER_SIGNATURE + AMPERSAND + OAUTH_CALLBACK;

		PostMethod firstMethod = new PostMethod(url);
		// firstMethod.addRequestHeader("Accept-Language","fr-FR" );

		System.out.println("Email where authentication code will sent->"
				+ useremail);
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><ns1:Tokens xmlns:ns1='http://www.hp.com/schemas/imaging/con/cloud/onramp/2009/12/20'><ns1:Temp ns1:version='0.57'>"
				+ "<ns1:EmailVerificationConfig><ns1:EmailAddress>"
				+ useremail
				+ "</ns1:EmailAddress><ns1:LinkTemplate>hpeprint://eAppOniPhone?vc=#VerificationCode#</ns1:LinkTemplate><ns1:VerificationCodeLocation>InLink</ns1:VerificationCodeLocation>"
				+ "<ns1:Language>en</ns1:Language></ns1:EmailVerificationConfig></ns1:Temp></ns1:Tokens>";
		firstMethod.setRequestEntity(new StringRequestEntity(xml));

		String responseData = null;
		try {
			System.out
					.println("\nInvoking CPG Server to get the temp token ....\n The URL is\n"
							+ url);
			System.out.println(httpclient.executeMethod(firstMethod));

		} catch (HttpException e1) {
			e1.printStackTrace();
			System.out.println("Failed!!");
			System.exit(0);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("Failed!!");
			System.exit(0);
		}

		try {
			responseData = firstMethod.getResponseBodyAsString();
			System.out.println("\n\nGot the temp token !!\n " + responseData);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed!!");
			System.exit(0);
		}

		String[] requestTokenData = responseData.split("&");

		System.out
				.println("\n\nEntering the verification code received through Email -> "
						+ useremail + " ->");
		/*
		 * Scanner in = new Scanner(System.in); String verificationCode =
		 * in.next();
		 */
		verificationCode = dbc.getverifcationtoken(useremail);
		verificationCode = "oauth_verifier=" + verificationCode;
		String oAuthToken = requestTokenData[1].split("=")[1];

		System.out.println(oAuthToken);
		String thirdMethodURL = ACCESSTOKEN_URL + OAUTH_CONSUMER_KEY
				+ MOB_CONSUMER_KEY + OAUTH_SIGNATURE_METHOD + AMPERSAND
				+ OAUTH_CALLBACK + AMPERSAND + verificationCode + AMPERSAND
				+ requestTokenData[0] + AMPERSAND + OAUTH_SIGNATURE
				+ MOB_CONSUMER_SIGNATURE + OAuthCodec.oauthEncode(oAuthToken);

		PostMethod thirdMethod = new PostMethod(thirdMethodURL);

		System.out.println(thirdMethodURL);
		String[] accessToken = null;

		try {
			System.out.println(httpclient.executeMethod(thirdMethod));
			String response = thirdMethod.getResponseBodyAsString();
			System.out.println(response);
			accessToken = response.split("&");
			System.out.println(accessToken[0] + accessToken[1]);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed!!");
			System.exit(0);
		}

		String resp2 = accessToken[0];
		String resp3 = accessToken[1];
		String[] resp2str = resp2.split("&");

		System.out.println(OAUTH_CONSUMER_KEY + MOB_CONSUMER_KEY);
		String oauthHeader = OAUTH_CONSUMER_KEY + MOB_CONSUMER_KEY
				+ ",oauth_signature_method=PLAINTEXT," + resp2str[0] + COMMA
				+ OAUTH_SIGNATURE + MOB_CONSUMER_SIGNATURE
				+ OAuthCodec.oauthEncode(resp3.split("=")[1]);

		System.out
				.println("\n\nThe oauth Access token is generated!!!!! =\n\n\n"
						+ "OAuth " + oauthHeader);
		String final_oauth_header = "OAuth " + oauthHeader;
		// Write Access Token to file
		try {

			PrintWriter out = new PrintWriter(new FileWriter(
					"c:/accesstoken.txt"));
			System.out.println(final_oauth_header);
			out.println(final_oauth_header);
			out.close();
		} catch (IOException e) {
			System.out.println("Error during reading/writing");
		}
		return final_oauth_header;
	}

	// Method to store printer details
	public String storecredentials(String pstorefile, String pEmailAddress,
			String sip_auth) throws IOException {
		Properties prop = new Properties();
		FileInputStream ff = new FileInputStream(new File(pstorefile));
		PrintWriter out = new PrintWriter(new FileWriter(pstorefile, true));
		System.out.println("Storing the oauth for future reference");
		prop.setProperty(pEmailAddress, sip_auth);
		prop.store(out, null);
		out.close();
		// f.close();
		prop.load(ff);
		// ff.close();
		System.out.println("Printer's  Auth is "
				+ prop.getProperty(pEmailAddress));
		return prop.getProperty(pEmailAddress);

	}

	public String oauthtest(String stackdetails, String pstorefile,
			String pEmailAddress, String authtype, String useremail) {

		System.out.println("inside oauth class = " + stackdetails);
		System.out.println("reading stack details sip consumer key"
				+ SIP_CONSUMER_SIGNATURE);
		String str = "dummy oauth";

		return str;

	}

	public String getAuth(String stackdetails, String pstorefile,
			String pEmailAddress, String authtype, String useremail)
			throws IOException, FileNotFoundException, HttpException {
		
		System.out.println("OAuthGen class reading stack" + stackdetails);
		 
		FileInputStream f = new FileInputStream(new File(pstorefile));
//		shardurl = dbc.getsharddetails(pEmailAddress);
		
		String oauth = null, key = null;
		String mobauthcredentials = pEmailAddress.split("@")[0] + "_"
				+ useremail.split("@")[0];

		printer_details.load(f);

		try {
			if (authtype.equalsIgnoreCase("sip")) {
				if (printer_details.getProperty(pEmailAddress) == null) {
					shardurl = dbc.getsharddetails(pEmailAddress);
					oauth = get_sipoauth(pEmailAddress);
					key = pEmailAddress;
					storecredentials(pstorefile, key, oauth);
				} else {
					oauth = printer_details.getProperty(pEmailAddress);
					System.out.println("Oauth is present for the printer \n" + oauth);
					
					
					
				}

			} else if (authtype.equalsIgnoreCase("mob")) {
				if (printer_details.getProperty(mobauthcredentials) == null) {
					shardurl = dbc.getsharddetails(pEmailAddress);
					oauth = get_moboauth(useremail);
					key = mobauthcredentials;
					storecredentials(pstorefile, key, oauth);
				} else {
					oauth = printer_details.getProperty(mobauthcredentials);
					System.out.println("Oauth is present for the printer \n" + oauth);
				}

			}

		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return oauth;
	}
  
}
