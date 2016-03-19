package com.hp.automation.fleet.onramp.ngdc;

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
import org.apache.log4j.Logger;
import org.neo4j.cypher.internal.compiler.v2_1.perty.printToString;
import org.springframework.security.oauth.common.OAuthCodec;

public class OAuthGen {
	static Logger log = Logger.getLogger(OAuthGen.class.getName());
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

	// setup_details sd_obj = new setup_details();
	Properties server_prop = new Properties();
	Properties printer_details = new Properties();
	Properties printer_credentials = new Properties();

	DBConnector dbc;

	public OAuthGen(String spropFileName, String printercrdfile)
			throws FileNotFoundException, IOException {
		server_prop.load(new FileInputStream(spropFileName));
		printer_credentials.load(new FileInputStream(printercrdfile));
		ONRAMP_IP = this.server_prop.getProperty("ONRAMP_IP");
		OFFRAMP_IP = this.server_prop.getProperty("OFFRAMP_IP");
		SIP_CONSUMER_KEY = this.server_prop.getProperty("SIP_CONSUMER_KEY");
		SIP_CONSUMER_SIGNATURE = this.server_prop
				.getProperty("SIP_CONSUMER_SIGNATURE");
		MOB_CONSUMER_KEY = this.server_prop.getProperty("MOB_CONSUMER_KEY");
		MOB_CONSUMER_SIGNATURE = this.server_prop
				.getProperty("MOB_CONSUMER_SIGNATURE");
		// this.USER_EMAIL = this.server_prop.getProperty("USER_EMAIL");
		EPC_HOST = this.server_prop.getProperty("EPC_HOST");
		EPC_PORT = this.server_prop.getProperty("EPC_PORT");
		TRUSTSTORE = this.server_prop.getProperty("TRUSTSTORE");
		KEYSTORE = this.server_prop.getProperty("KEYSTORE");
		PROXY_HOST = this.server_prop.getProperty("PROXY_HOST");
		PROXY_PORT = Integer.valueOf(Integer.parseInt(this.server_prop
				.getProperty("PROXY_PORT")));
		System.setProperty(this.TRUSTSTORE_STR, this.TRUSTSTORE);
		dbc = new DBConnector(spropFileName);

	}

	public String get_sipoauth(String printerEmailAddress, String stackname)
			throws HttpException, IOException, JSchException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		String prn_temp = null;

		if (stackname.equalsIgnoreCase("stage1")) {
			prn_temp = printer_credentials.getProperty(printerEmailAddress);
			this.PRINTER_ID = prn_temp.split(",")[0];
			this.PRINTER_KEY = prn_temp.split(",")[1];
			log.info("reading printer details for STAGE1 printer = "
					+ prn_temp);

		} else {
			prn_temp = dbc.getCredentials(shardurl, printerEmailAddress);
			this.PRINTER_ID = prn_temp.split(",")[0];
			this.PRINTER_KEY = prn_temp.split(",")[1];

		}

		this.PRINTER_EMAIL_ADDRESS = printerEmailAddress;

		log.info("ONRAMP URL " + this.ONRAMP_IP);
		log.info("OFFRAMP URL " + this.OFFRAMP_IP);
		log.info("PRINTER EMAILADDRESS " + this.PRINTER_EMAIL_ADDRESS);
		log.info("PRINTER ID=" + this.PRINTER_ID);
		log.info("PRINTER KEY=" + this.PRINTER_KEY);
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

		log.info("First Oauth http call = "+REQUEST_URL + this.OAUTH_CONSUMER_KEY
				+ this.SIP_CONSUMER_KEY + this.OAUTH_SIGNATURE_METHOD
				+ this.AMPERSAND + this.OAUTH_SIGNATURE
				+ this.SIP_CONSUMER_SIGNATURE + this.AMPERSAND
				+ this.OAUTH_CALLBACK);

		PostMethod firstMethod = new PostMethod(REQUEST_URL
				+ this.OAUTH_CONSUMER_KEY + this.SIP_CONSUMER_KEY
				+ this.OAUTH_SIGNATURE_METHOD + this.AMPERSAND
				+ this.OAUTH_SIGNATURE + this.SIP_CONSUMER_SIGNATURE
				+ this.AMPERSAND + this.OAUTH_CALLBACK);

		String responseData = "";
		try {
			int firstres = httpclient.executeMethod(firstMethod);
			log.info(firstres);
			log.info(firstMethod.getStatusCode());
			responseData = firstMethod.getResponseBodyAsString();
		} catch (HttpException e1) {
			e1.printStackTrace();
			log.info("Failed!!");
			System.exit(0);
		} catch (IOException e1) {
			e1.printStackTrace();
			log.info("Failed!!");
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
			// log.info(secondMethod.getResponseHeader("Location").getValue());
			verificationCode = secondMethod.getResponseHeader("Location")
					.getValue().split("\\?")[1].split("&")[1];
		} else {
			try {
				log.info(secondMethod.getResponseBodyAsString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			log.info("Failed!!");
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
			// log.info(accessToken[0] + accessToken[1]);
		} catch (IOException e) {
			e.printStackTrace();
			log.info("Failed!!");
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

	public String get_moboauth(String useremail, String stackname)
			throws JSchException, IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
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

		log.info("Email where authentication code will sent->"
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
			log.info(httpclient.executeMethod(firstMethod));

		} catch (HttpException e1) {
			e1.printStackTrace();
			log.info("Failed!!");
			System.exit(0);
		} catch (IOException e1) {
			e1.printStackTrace();
			log.info("Failed!!");
			System.exit(0);
		}

		try {
			responseData = firstMethod.getResponseBodyAsString();
			log.info("\n\nGot the temp token !!\n " + responseData);
		} catch (IOException e) {
			e.printStackTrace();
			log.info("Failed!!");
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

		log.info(oAuthToken);
		String thirdMethodURL = ACCESSTOKEN_URL + OAUTH_CONSUMER_KEY
				+ MOB_CONSUMER_KEY + OAUTH_SIGNATURE_METHOD + AMPERSAND
				+ OAUTH_CALLBACK + AMPERSAND + verificationCode + AMPERSAND
				+ requestTokenData[0] + AMPERSAND + OAUTH_SIGNATURE
				+ MOB_CONSUMER_SIGNATURE + OAuthCodec.oauthEncode(oAuthToken);

		PostMethod thirdMethod = new PostMethod(thirdMethodURL);

		log.info(thirdMethodURL);
		String[] accessToken = null;

		try {
			log.info(httpclient.executeMethod(thirdMethod));
			String response = thirdMethod.getResponseBodyAsString();
			log.info(response);
			accessToken = response.split("&");
			log.info(accessToken[0] + accessToken[1]);
		} catch (IOException e) {
			e.printStackTrace();
			log.info("Failed!!");
			System.exit(0);
		}

		String resp2 = accessToken[0];
		String resp3 = accessToken[1];
		String[] resp2str = resp2.split("&");

		log.info(OAUTH_CONSUMER_KEY + MOB_CONSUMER_KEY);
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
			log.info(final_oauth_header);
			out.println(final_oauth_header);
			out.close();
		} catch (IOException e) {
			log.info("Error during reading/writing");
		}
		return final_oauth_header;
	}

	// Method to store printer details
	public String storecredentials(String pstorefile, String pEmailAddress,
			String sip_auth) throws IOException {
		Properties prop = new Properties();
		FileInputStream ff = new FileInputStream(new File(pstorefile));
		PrintWriter out = new PrintWriter(new FileWriter(pstorefile, true));
		log.info("Storing the oauth for future reference");
		prop.setProperty(pEmailAddress, sip_auth);
		prop.store(out, null);
		out.close();
		// f.close();
		prop.load(ff);
		// ff.close();
		log.info("Printer's  Auth is "
				+ prop.getProperty(pEmailAddress));
		return prop.getProperty(pEmailAddress);

	}

	/*
	 * public String oauthtest(String stackdetails, String pstorefile, String
	 * pEmailAddress, String authtype, String useremail) {
	 * 
	 * log.info("inside oauth class = " + stackdetails);
	 * log.info("reading stack details sip consumer key" +
	 * SIP_CONSUMER_SIGNATURE); String str = "dummy oauth";
	 * 
	 * return str;
	 * 
	 * }
	 */

	public String getAuth(String stackdetails, String pstorefile,
			String pEmailAddress, String authtype, String useremail)
			throws IOException, FileNotFoundException, HttpException {

		// log.info("OAuthGen class reading stack " + stackdetails);
		String stackname = stackdetails.split("\\.")[0];
		log.info(stackname + "\n");
		FileInputStream f = new FileInputStream(new File(pstorefile));

		if (!stackname.equalsIgnoreCase("stage1")) {
			shardurl = dbc.getsharddetails(pEmailAddress);
		}

		String oauth = null, key = null;
		String mobauthcredentials = pEmailAddress.split("@")[0] + "_"
				+ useremail.split("@")[0];

		printer_details.load(f);

		try {
			if (authtype.equalsIgnoreCase("sip")) {
				if (printer_details.getProperty(pEmailAddress) == null) {

					// shardurl = dbc.getsharddetails(pEmailAddress);
					oauth = get_sipoauth(pEmailAddress, stackname);
					key = pEmailAddress;
					storecredentials(pstorefile, key, oauth);

				} else {
					oauth = printer_details.getProperty(pEmailAddress);
					log.info("Oauth key is found for the printer =  "
							+ pEmailAddress + "\n");

				}

			} else if (authtype.equalsIgnoreCase("mob")) {
				if (printer_details.getProperty(mobauthcredentials) == null) {
					// shardurl = dbc.getsharddetails(pEmailAddress);
					oauth = get_moboauth(useremail, stackname);
					key = mobauthcredentials;
					storecredentials(pstorefile, key, oauth);
				} else {
					oauth = printer_details.getProperty(mobauthcredentials);
					log.info("Oauth is present for the printer \n"
							+ oauth);
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
