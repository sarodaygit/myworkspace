package com.hp.automation.tutorials.qc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

//import org.opensaml.artifact.NullArgumentException;


/**
 * ALM web service interaction.
 * Allows to open a connection to ALM REST web service through user authentication and
 * starting a session to use for further requests.
 * 
 * @author Bjorn Weitzel (bjoern.weitzel@hp.com)
 * @version 21th November 2011
 * @company Hewlett-Packard Company
 */
public class ALMConnection extends AbstractALM {

	// Temporary error handler. For framework usage remove it and extends AbstractALM
	//public void errorHandler(Throwable t) {
	//	System.out.println("ERROR:"+t.getCause()+" - "+t.getMessage());
	//}
	
	/**
	 * User to use for authentication 
	 */
	private String almUser = null;
	/**
	 * Password for almUser
	 */
	private String almPassword = null;
  /**
   * Domain to be used
   */
  private String almDomain = null;
  /**
   * Project of almDomain to be used
   */
  private String almProject = null;
  /**
   * URL of the ALM server
   */
  private String serverURL = null;
  /**
   * Port of the application server ALM is running on
   */
  private int serverPort = 0;
  
	/**
	 * User is authenticated
	 */
	private boolean isAuthenticated = false;
  /**
   * User is connected
   */
  private boolean isConnected = false;
  
  /**
   * LWSSO cookie
   */
  private String cookieLWSSO_COOKIE_KEY = null;
  /**
   * QC session cookie
   */
  private String cookieQCSession = null;
  

  /**
   * Creates a connection to be used for interaction with ALM REST web service.
   */
  public ALMConnection() {
  	super();
  }
  
  /**
   * Creates a connection to be used for interaction with ALM REST web service.
   * 
   * @param almUser ALM user to log on with
   * @param almPassword Password for almUser
   * @param almDomain ALM domain to be used
   * @param almProject ALM project to be used
   * @param serverURL URL of the server ALM is running on
   * @param serverPort Port of the application server ALM is running on
   */
  public ALMConnection(String almUser, String almPassword, String almDomain,
			String almProject, String serverURL, int serverPort) {
		super();
		this.almUser = almUser;
		this.almPassword = almPassword;
		this.almDomain = almDomain;
		this.almProject = almProject;
		this.serverURL = serverURL;
		this.serverPort = serverPort;
	}
  

  /**
   * @return ALM user to be logged on with.
   */
  public String getAlmUser() {
		return this.almUser;
	}

	/**
	 * @param almUser ALM user to be logged on with.
	 */
	public void setAlmUser(String almUser) {
		if (this.isConnected)
			throw new IllegalStateException("Not possible to change an established connection.");
		this.almUser = almUser;
	}

	/**
	 * @return ALM password for almUser to be used.
	 */
	public String getAlmPassword() {
		return this.almPassword;
	}

	/**
	 * @param almPassword Password for almUser to be used for login.
	 */
	public void setAlmPassword(String almPassword) {
		if (this.isConnected)
			throw new IllegalStateException("Not possible to change an established connection.");
		this.almPassword = almPassword;
	}

	/**
	 * @return ALM domain to be used.
	 */
	public String getAlmDomain() {
		return this.almDomain;
	}

	/**
	 * @param almDomain ALM domain to be used.
	 */
	public void setAlmDomain(String almDomain) {
		if (this.isConnected)
			throw new IllegalStateException("Not possible to change an established connection.");
		this.almDomain = almDomain;
	}

	
	/**
	 * @return ALM project to be used
	 */
	public String getAlmProject() {
		return this.almProject;
	}

	/**
	 * @param almProject Specifies the project to log on to
	 */
	public void setAlmProject(String almProject) {
		if (this.isConnected)
			throw new IllegalStateException("Not possible to change an established connection.");
		this.almProject = almProject;
	}

	/**
	 * @return URL of the server ALM is running on
	 */
	public String getServerURL() {
		return this.serverURL;
	}

	/**
	 * @param serverURL URL of the server ALM is running on
	 */
	public void setServerURL(String serverURL) {
		if (this.isConnected)
			throw new IllegalStateException("Not possible to change an established connection.");
		this.serverURL = serverURL;
	}

	/**
	 * @return Port of the application server ALM on the server
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * @param serverPort Port of the application server ALM on the server
	 */
	public void setServerPort(int serverPort) {
		if (this.isConnected)
			throw new IllegalStateException("Not possible to change an established connection.");
		this.serverPort = serverPort;
	}

	/**
	 * @return True if authenticated against ALM
	 */
	public boolean isAuthenticated() {
		return this.isAuthenticated;
	}

	/**
	 * @return True if connection established
	 */
	public boolean isConnected() {
		return this.isConnected;
	}
  
	/**
	 * @return Assembled server URL based on serverUrl, serverPort and /qcbin/
	 */
	public String buildBasicURL() {
	  return this.serverURL+":"+this.serverPort+"/qcbin/";
	}
	
	/**
	 * @return Assembled server URL based on serverUrl, serverPort and /qcbin/ and domain+project
	 */
	public String buildFullURL() {
	  StringBuilder sb = new StringBuilder();
	  
	  sb.append(buildBasicURL());
	  
	  if(this.isConnected)
	  	sb.append("rest/domains/"+this.almDomain+"/projects/"+this.almProject+"/");
	  return sb.toString();
	}
	
	
	/**
	 * Authenticate against ALM web service 
	 * 
	 * @return True if authentication was successful
	 */
	public boolean Authenticate() {
		// Check for already established connection
		if (this.isConnected)
			throw new IllegalStateException("Connection already established.");
		if (this.serverURL == null || this.serverPort == 0)
			throw new IllegalStateException("No server information set.");
		if (this.almUser == null || this.almPassword == null)
			throw new IllegalStateException("No user information set.");
		if (this.almDomain == null || this.almProject == null)
			throw new IllegalStateException("No ALM project information set.");


		String authPoint = buildFullURL() + "authentication-point/authenticate";
		HttpURLConnection con = null;
		
		// Prepare authentication request
		try {
			con = prepareHttpConnection(authPoint, "GET");

		  byte[] authenticationBytes = (this.almUser + ":" + this.almPassword).getBytes("UTF-8");
		  String encodedAuthentication = "Basic " + Base64Converter.encode(authenticationBytes);
      con.setRequestProperty("Authorization", encodedAuthentication);
		} catch (Exception e) {
			errorHandler(e);
		}

    Response res = getHTTPResponse(con);

		try {       
      this.isAuthenticated = (res.getStatusCode() == HttpURLConnection.HTTP_OK);
      if(!this.isAuthenticated)
      	return this.isAuthenticated;
      
      // Get LWSSO Cookie
      Iterable<String> newCookies = res.getResponseHeaders().get("Set-Cookie");
      if (newCookies != null) {
        for (String cookie : newCookies) {
        	if (cookie.startsWith("LWSSO")) {
        		this.cookieLWSSO_COOKIE_KEY = cookie; 
        		break;
        	}
        }
      }
      
		  con.disconnect();
		} catch (Exception e) {
		  errorHandler(e);
		}
  
		return this.isAuthenticated;
	}

	/**
	 * Establishes a web service session with ALM.
	 * @return Session started successfully
	 */
	public boolean StartSession() {
		if (this.isConnected)
			throw new IllegalStateException("Connection already established.");
		if (!this.isAuthenticated)
			throw new IllegalStateException("Authenticate first");
		
		String sessionPoint = buildFullURL() + "/rest/site-session";
		HttpURLConnection con = null;
		
		// Session assembling
		try {
			con = prepareHttpConnection(sessionPoint, "POST");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      con.setRequestProperty("Content-Length", "0");
		} catch (Exception e) {
			errorHandler(e);
		}
		
		// Send authentication data and receive cookie
    Response res = getHTTPResponse(con);

		try {       
      this.isConnected = (res.getStatusCode() == HttpURLConnection.HTTP_OK);
      if (!this.isConnected)
      	return this.isConnected;
      
      // Get Session cookie
      Iterable<String> newCookies = res.getResponseHeaders().get("Set-Cookie");
      if (newCookies != null) {
        for (String cookie : newCookies) {
        	if (cookie.startsWith("QCSession")) {
        		this.cookieQCSession = cookie; 
        		break;
        	}
        }
      }
      
		  con.disconnect();
		} catch (Exception e) {
		  errorHandler(e);
		}
		
		return this.isConnected;
	}
	
  /**
   * Ends the session with the web service.
   * 
   * @return Closes the session on server side and restores connection object to initial status.
   * @throws Exception Connection or server error during logout request
   */
  public boolean Logout() throws Exception {
		String logoutPoint = buildBasicURL() + "/authentication-point/logout";
		HttpURLConnection con = null;
		Response res = null;
		
		try {
			con = prepareHttpConnection(logoutPoint, "GET");
			con.connect();
			res = retrieveHtmlResponse(con);
			
			// Zur�cksetzen der Verbindung
			this.almUser = null;
			this.almPassword = null;
			this.almDomain = null;
			this.almProject = null;
			this.serverURL = null;
			this.serverPort = 0;
			this.cookieLWSSO_COOKIE_KEY = null;
			this.cookieQCSession = null;
			this.isAuthenticated = false;
			this.isConnected = false;
		} catch (Exception e) {
			errorHandler(e);
			return false;
		}

    return (res.getStatusCode() == HttpURLConnection.HTTP_OK);
}
	
  /**
   * Getting a HTTP Response from a HTTPURLConnecion.
   * @param con Connection to get the response from
   * @return Response of the connection attemt
   */
  private Response getHTTPResponse(HttpURLConnection con) {
  	Response res = null;
  	try {
  		con.connect();
  		res = retrieveHtmlResponse(con);
  	} catch (Exception e) {
  		errorHandler(e);
  		res = new Response();
  		res.setFailure(e);
  	}
		return res;
  }
  
  /**
   * Reading and evaluation HTTP response
   * @param con Already established HTTP URL connection awaiting a response
   * @return A response from the server to the previously submitted HTTP request
   * @throws Exception
   */
  private Response retrieveHtmlResponse(HttpURLConnection con) throws Exception {
    Response res = new Response();
    res.setStatusCode(con.getResponseCode());
    res.setResponseHeaders(con.getHeaderFields());
    InputStream inputStream;
    
    // Select the source of the input bytes, first try "regular" input.
    try {
        inputStream = con.getInputStream();
    } catch (Exception e) {
        inputStream = con.getErrorStream();
        res.setFailure(e);
    }
    
    // This takes the data from the stream and stores it in a byte[] inside the response.
    ByteArrayOutputStream container = new ByteArrayOutputStream();
    byte[] buf = new byte[1024];
    int read;
    while ((read = inputStream.read(buf, 0, 1024)) > 0) {
        container.write(buf, 0, read);
    }
    
    res.setResponseData(container.toByteArray());
    return res;
  }
	
  
  /**
   * @param url Address of the connections destination
   * @param method HTTP method (GET, POST, etc.)
   * @return A prepared HTTPURLConnection
 * @throws NullArgumentException 
   */
  private HttpURLConnection prepareHttpConnection(String url, String method) throws NullArgumentException {
  	HttpURLConnection con = null;
		
  	try {
			con = (HttpURLConnection) new URL(url).openConnection();
			if (method.toLowerCase().equals("post")) {
	  		con.setRequestProperty("Content-Type", "application/xml");
	  		con.setRequestProperty("Accept", "application/xml");
			}
		} catch (Exception e) {
			errorHandler(e);
		}
  	
  	return prepareHttpConnection(con, method);
  }
  
  
  /**
   * @param con A connection to be prepared with request method and required cookies
   * @param method HTTP method (GET, POST, etc.)
   * @return A prepared HTTPURLConnection
 * @throws NullArgumentException 
   */
  private HttpURLConnection prepareHttpConnection(HttpURLConnection con, String method) throws NullArgumentException {
  	if (con == null)
  		throw new NullArgumentException("No connection object.");
  
  	// Set method and cookies if required
  	try {
			con.setRequestMethod(method);
				
			if (this.isAuthenticated)
				con.setRequestProperty("Cookie", this.cookieLWSSO_COOKIE_KEY);
			else if (this.isConnected)
				con.setRequestProperty("Cookie", this.cookieQCSession);
		} catch (Exception e) {
			errorHandler(e);
		}
  	
  	return con;
  }
  
 
  /**
   * @param fieldMap Map consisting of values to be sent to the web service (key, value)
   * @return Entity object based on the given input values
   */
  private Entity BuildTestRunResultEntity(Map<String, String> fieldMap) {
  	Entity entity = new Entity();
  	Entity.Fields fields = new Entity.Fields();
  	Entity.Fields.Field field = new Entity.Fields.Field(); 
  	
  	Iterator<Entry<String, String>> headersIterator = fieldMap.entrySet().iterator();
  	while (headersIterator.hasNext()) {
  		Entry<String, String> header = headersIterator.next();
  		field = new Entity.Fields.Field(); 
  		field.setName(header.getKey());
  		field.addValue(header.getValue());
  		fields.addField(field);
  	}	
  		
  	entity.setFields(fields);
  	entity.setType("run");
  	return entity;
  }
  
  
  /**
   * @return Current system time
   */
  private String GetCurrentDateTime() {
  	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    Date date = new Date();
    return dateFormat.format(date);
  }

  /**
   * @param test_id ID of the design test from which the instance is derived
   * @param cycle_id ID of the TestSet containing the instance
   * @param test_instance Instance of the test from the execution grid
   * @param testcycl_id Cycle id of the test instance
   * @param owner User who initiated the test
   * @param status Status (default: N/A, Blocked, Failed, No Run, Not Completed, Passed)
   * @param test_config_id ID of the configuration that was run
   * @return True if adding results has been successful
   */
  public boolean AddVAPITestRun(int test_id, int cycle_id, int test_instance, 
  		int testcycl_id, String owner, String status, String test_config_id) {
  	return AddVAPITestRun(String.valueOf(test_id), String.valueOf(cycle_id), String.valueOf(test_instance),
  			String.valueOf(testcycl_id), owner, status, test_config_id);
  }
  
  /**
   * @param test_id ID of the design test from which the instance is derived
   * @param cycle_id ID of the TestSet containing the instance
   * @param test_instance Instance of the test from the execution grid
   * @param testcycl_id Cycle id of the test instance
   * @param owner User who initiated the test
   * @param status Status (default: N/A, Blocked, Failed, No Run, Not Completed, Passed)
   * @param test_config_id ID of the configuration that was run
   * @return True if adding results has been successful
   */
  public boolean AddVAPITestRun(String test_id, String cycle_id, String test_instance, 
    String testcycl_id, String owner, String status, String test_config_id) {
  	boolean success = false;
  	String resultPoint = buildFullURL() + "runs";
  	HttpURLConnection con = null;
  	byte[] data = null;
  	
  	// Add fields required to create a new result set for the test run
  	Map<String, String> fieldMap = new HashMap<String, String>();
  	fieldMap.put("test-id", test_id);  // ID of the design test from which the instance is derived
  	fieldMap.put("cycle-id", cycle_id);  // ID of the TestSet containing the instance
  	fieldMap.put("test-instance", test_instance); // Instance of the test from the execution grid
  	fieldMap.put("testcycl-id", testcycl_id);  // Cycle id of the test instance
  	fieldMap.put("name", "Run: " + GetCurrentDateTime()); // Name of the run
  	fieldMap.put("owner", owner);  // User who performed the test
  	fieldMap.put("subtype-id","hp.qc.run.VAPI-XP-TEST");  // Subtype of the instance
  	fieldMap.put("status", status);  // Status (default: N/A, Blocked, Failed, No Run, Not Completed, Passed)
  	fieldMap.put("test-config-id", test_config_id);  // ID of the configuration that was run
  	
  	// Create entity based on the given information
  	Entity entity = null;
  	String entityXML = null;
		try {
			entity = BuildTestRunResultEntity(fieldMap);
			entityXML = EntityMarshallingUtils.unmarshal(Entity.class, entity).trim();
		} catch (Exception e) {
			errorHandler(e);
		}

		// Send the data to the REST web service
  	try {
  		con = prepareHttpConnection(resultPoint, "POST");
  		con.setDoOutput(true);
  		
  		data = entityXML.getBytes();
      OutputStream out = con.getOutputStream();
      out.write(data);
      out.flush();
      out.close();
  	} catch (Exception e) {
  		errorHandler(e);
  	}
 
  	// Check response if submission was successful
  	try {
  		Response res = getHTTPResponse(con);  	
  		success = res.getStatusCode() == HttpURLConnection.HTTP_CREATED;
  		if (!success)
  			throw new Exception(res.toString());  		
  	} catch (Exception e) {
  		errorHandler(e);
  	}
  	return success;
  }
}
