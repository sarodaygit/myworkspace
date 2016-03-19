package com.hp.de.automation.onramp.threading.v2;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
//import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.*;
import org.apache.commons.io.FileUtils;
import org.jdom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;



public class JschJdbc {
	private static final String xsdns = "http://www.w3.org/2001/XMLSchema";
	JSch jsch ;
	Properties config,stack,sshdetails;
	String dbuser;
	String dbpwd;
	String driverName;
	int local_port;
	int mysql_port;
	String hostusername;
	String ppkfile;
	int ppkport;
	String shardhost;
	String printerEmailAddress;
	

	
	
	
	public JschJdbc(String p_stackdetails) throws FileNotFoundException, IOException{
		jsch = new JSch();
		config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		stack = new Properties();
		stack.load(new FileInputStream(p_stackdetails));
		driverName = "com.mysql.jdbc.Driver";
		hostusername = stack.getProperty("ppkuser");
		ppkfile = stack.getProperty("pemfile");
		ppkport = 22;
		dbuser = "root";
		dbpwd = "opelin";
		local_port = 5656;
		mysql_port = 3306;
	}
	
	public Session getSession(String p_host, String p_user, int p_port, String p_ppkfile ) throws JSchException, IOException{
		Session session = null;
		File file = new File(p_ppkfile);
		final byte[] prvkey = FileUtils.readFileToByteArray(file);
		final byte[] emptyPassPhrase = new byte[0];
		jsch.addIdentity(p_host, prvkey, null, emptyPassPhrase);
		session = jsch.getSession(p_user, p_host, p_port);
		session.setConfig(config);
		session.connect();
		return session;
	}
	
	public String getShard(String pEmailAddress)
	throws SQLException, InstantiationException,
	IllegalAccessException, ClassNotFoundException, JSchException,
	FileNotFoundException, IOException 
	{
		
		String cpgdbhost = stack.getProperty("CPGDB");
		System.out.println("CPG DB_HOST" + cpgdbhost);
		String cpgDBurl = "jdbc:mysql://localhost:" + local_port + "/cpgDB";
		String app_sql="select s.shardId from Device d, Shard s where d.code = s.shardid and d.deviceEmailId = '" + pEmailAddress + "'";
		int shardid=0;
		Statement stmt = null;
		String rhost = cpgdbhost;
		Connection conn = null;
		Session session = getSession(cpgdbhost, hostusername, ppkport, ppkfile);
		session.setPortForwardingL(local_port, rhost, mysql_port);
		Class.forName(driverName).newInstance();
		conn = (Connection) DriverManager.getConnection(cpgDBurl, dbuser,dbpwd);
		stmt = (Statement) conn.createStatement();
		ResultSet rs = stmt.executeQuery(app_sql);
		while (rs.next()) 
		{
			shardid = rs.getInt(1);
		}
		
		String shardlabel = "CPGDBSHARD" + Integer.toString(shardid);
		
		shardhost =  stack.getProperty(shardlabel);
		
		conn.close();
		session.disconnect();
		
		return shardhost;
	}
	
	public String getCredentials( String pEmailAddress) throws JSchException, InstantiationException,
	IllegalAccessException, ClassNotFoundException, SQLException, IOException{
		
		
		String pcd = null;
		
		String cpgDBurl = "jdbc:mysql://localhost:" + local_port + "/cpgDB_SHARD";
		String app_sql="select PrinterId, PrinterKey from Printer where  PrinterEmailAddress = '" +pEmailAddress+"'";
		Statement stmt = null;
		System.out.println("Oauth for Printer " + pEmailAddress + " is not present");
		System.out.println("Getting Shard for the printer");
		String dbhost = getShard(pEmailAddress);
//		System.out.println("Shard Host for printer " + pEmailAddress +" is " + dbhost);
		String rhost = dbhost;
		Connection conn = null;
		Session session = getSession(dbhost, hostusername, ppkport, ppkfile);
		session.setPortForwardingL(local_port, rhost, mysql_port);
		Class.forName(driverName).newInstance();
		conn = (Connection) DriverManager.getConnection(cpgDBurl, dbuser,dbpwd);
		stmt = (Statement) conn.createStatement();
		ResultSet rs = stmt.executeQuery(app_sql);
		
		while (rs.next()) 
		{
			pcd = rs.getString(1) + "," +rs.getString(2);
			
		}
		
		conn.close();
		session.disconnect();
		System.out.println("PrinterEmail Address = " + pEmailAddress);
		System.out.println("PrinterId = " + pcd.split(",")[0]);
		System.out.println("Printerkey = " + pcd.split(",")[1]);
		return pcd;
//		return options;
	}
	
	public void getPrinterFamily2(String pEmailAddress,String fname) throws InstantiationException, IllegalAccessException, 
	ClassNotFoundException, FileNotFoundException, SQLException, JSchException, IOException {
		
String pcd = null;
		
		String cpgDBurl = "jdbc:mysql://localhost:" + local_port + "/cpgDB_SHARD";
		String app_sql="Select ModelNumber,MakeAndModel from Printer where printerEmailAddress = '" +pEmailAddress+"'";
//		String app_sql="Select * from Printer where printerEmailAddress = '" +pEmailAddress+"'";
		Statement stmt = null;
		String dbhost = getShard(pEmailAddress);
		String rhost = dbhost;
		Connection conn = null;
		Session session = getSession(dbhost, hostusername, ppkport, ppkfile);
		session.setPortForwardingL(local_port, rhost, mysql_port);
		Class.forName(driverName).newInstance();
		conn = (Connection) DriverManager.getConnection(cpgDBurl, dbuser,dbpwd);
		stmt = (Statement) conn.createStatement();
		ResultSet rs = stmt.executeQuery(app_sql);
		Document xsd = RS2DOM.ResultSet2XSDDOM(rs);
		Document d = RS2DOM.ResultSet2DOM(rs);
		toXml( d,fname);
		toXsd(xsd);
		
		conn.close();
		session.disconnect();
		
//		return options;

		
	}

	public void getJobResults(String pEmailAddress,String fname) throws InstantiationException, IllegalAccessException, 
	ClassNotFoundException, FileNotFoundException, SQLException, JSchException, IOException {
		
String pcd = null;
		
		String cpgDBurl = "jdbc:mysql://localhost:" + local_port + "/cpgDB_SHARD";
//		String app_sql="Select ModelNumber,MakeAndModel from Printer where printerEmailAddress = '" +pEmailAddress+"'";
		String app_sql="SELECT d.jobId, d.id, fl.jobFlowLetState, d.state, d.docformat, d.NAME AS DocumentName, j.NAME FROM Document d   LEFT JOIN JobFlowLet fl ON fl.jobFlowLetId = SUBSTRING(d.jobFlowLetId, 4, 99)   JOIN Job j ON d.jobId = j.id   JOIN Printer p ON p.InternalPrinterID = j.printerId WHERE   p.printeremailaddress IN ('" + pEmailAddress +"')   ORDER BY d.`createdAt` DESC";
		Statement stmt = null;
		String dbhost = getShard(pEmailAddress);
		String rhost = dbhost;
		Connection conn = null;
		Session session = getSession(dbhost, hostusername, ppkport, ppkfile);
		session.setPortForwardingL(local_port, rhost, mysql_port);
		Class.forName(driverName).newInstance();
		conn = (Connection) DriverManager.getConnection(cpgDBurl, dbuser,dbpwd);
		stmt = (Statement) conn.createStatement();
		ResultSet rs = stmt.executeQuery(app_sql);
//		Document xsd = RS2DOM.ResultSet2XSDDOM(rs);
		Document d = RS2DOM.ResultSet2DOM(rs);
		toXml(d,fname);
//		toXsd(xsd);
		
		conn.close();
		session.disconnect();
		
//		return options;

		
	}
	
	
	private void toXml(Document d, String fname)
			throws TransformerFactoryConfigurationError {
		String fpath=null;
		try {
			Transformer myTransformer =	(TransformerFactory.newInstance()).newTransformer();
			System.out.println("\n\nXML document containing the result set = " +fname);
//			myTransformer.transform(new DOMSource(d),new StreamResult(new FileOutputStream(FileDescriptor.out)));
			File f = new File(fname);
			myTransformer.transform(new DOMSource(d),new StreamResult(f));
			fpath = f.getAbsolutePath(); 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		 
	}
	
	private void toXsd(Document xsd)
		throws TransformerFactoryConfigurationError {
		String fpath=null;
		try {
			Transformer myTransformer =	(TransformerFactory.newInstance()).newTransformer();
			
		//	myTransformer.transform(new DOMSource(xsd),	new StreamResult(new FileOutputStream(FileDescriptor.out)));
			File f = new File("C:\\test.xsd");
			myTransformer.transform(new DOMSource(xsd),	new StreamResult(f));
			 fpath = f.getAbsolutePath();
			 System.out.println(	"XML Schema instance describing the document test.xsd");
		} catch (Exception e) {
			e.printStackTrace();
		}
}
	
	public ArrayList<String[]> GetRowData(ResultSet rs) throws SQLException{
		ArrayList <String[]> result = new ArrayList<String[]>();
		
		int columnCount = rs.getMetaData().getColumnCount();
		
		
		while(rs.next())
		{
		    String[] row = new String[columnCount];
		    for (int i=0; i <columnCount ; i++)
		    {
		       row[i] = rs.getString(i + 1);
		       
		    }
		    result.add(row);
		}
		
		return result;
	}
	
	public ArrayList<String[]> GetFirstRow(ResultSet rs) throws SQLException{
		
		
		ArrayList <String[]> result = new ArrayList<String[]>();
		int columnCount = rs.getMetaData().getColumnCount();
		System.out.println("Column Count = " + columnCount);
		rs.next();
		String[] row = new String[columnCount];
		for (int i = 1; i <columnCount ; i++)
		{
			
			row[i] = rs.getString(i +1  );
			System.out.print(row[i]);

			
		}
		
		
		result.add(row);
		return result;
	}
	
	public String toHtml(ResultSet rs) throws SQLException{
		
		ResultSetMetaData  rsmd1 = rs.getMetaData();
		String tab_name = rsmd1.getTableName(1);
		String htm_rows = null;
		htm_rows = "<html><body><h4>Details "+tab_name+"</h4><table border='1'><tr>";
		int i =0;
		
//		System.out.println("\n"+rsmd1.getTableName(1)+"\n");
		
		for( i = 1;i <= rsmd1.getColumnCount();i++){
			htm_rows = htm_rows+ "<th>" + rsmd1.getColumnLabel(i) + "</th>";
		}
		 htm_rows =  htm_rows + "</tr><tr>";
		
		while (rs.next()) 
		{
			
			for( i = 1; i <= rsmd1.getColumnCount(); i++){
				htm_rows = htm_rows+ "<td>" + rs.getString(i) + "</td>";
			}
			 htm_rows =  htm_rows + "</tr>";
			
		}
		htm_rows =  htm_rows + "</table></body></html>";
//		writeOutput(htm_rows);
		return htm_rows;
		
	}
	
	public void writeOutput(String str) {

	       try {
	           FileOutputStream fos = new FileOutputStream("C:\\nagaraj\\workspace\\tutorial\\test.txt");
	           Writer out = new OutputStreamWriter(fos);
	           
	           out.write(str);
	           
	           out.close();
	       } catch (IOException e) {
	           e.printStackTrace();
	       }
	   }

	
	public Document getResultSet(String pEmailAddress, String app_sql) throws FileNotFoundException, 
	SQLException, InstantiationException,IllegalAccessException, ClassNotFoundException, JSchException, IOException
	{
		
String pcd = null;
		
		String cpgDBurl = "jdbc:mysql://localhost:" + local_port + "/cpgDB_SHARD";
		Statement stmt = null;
		String dbhost = getShard(pEmailAddress);
		String rhost = dbhost;
		Connection conn = null;
		Session session = getSession(dbhost, hostusername, ppkport, ppkfile);
		session.setPortForwardingL(local_port, rhost, mysql_port);
		Class.forName(driverName).newInstance();
		conn = (Connection) DriverManager.getConnection(cpgDBurl, dbuser,dbpwd);
		stmt = (Statement) conn.createStatement();
		ResultSet rs = stmt.executeQuery(app_sql);
		
		Document resd = RS2DOM.ResultSet2DOM(rs);
		
		
		conn.close();
		session.disconnect();
		
//		return options;

		
		return resd;
		
	}
	
	/*public void getdocstatus(String pEmailAddress) throws IOException, SQLException, InstantiationException, 
	IllegalAccessException, ClassNotFoundException, JSchException, IOException {
		
		String query = "SELECT d.jobId, d.createdAt, d.completedAt, d.notifyTime, HOUR(d.createdAt) AS `hour`,    d.comments, fl.jobFlowLetState, d.state, d.docformat, fl.assignedWorkerURI, d.NAME AS DocumentName,    j.NAME, p.printerId, p.SerialNumber, p.ModelNumber,    d.docUri, j.originator FROM Document d   LEFT JOIN JobFlowLet fl ON fl.jobFlowLetId = SUBSTRING(d.jobFlowLetId, 4, 99)   JOIN Job j ON d.jobId = j.id   JOIN Printer p ON p.InternalPrinterID = j.printerId WHERE   " +
				"p.printeremailaddress IN ('" +pEmailAddress + "')   ORDER BY d.`createdAt` DESC";
		Document dd = getResultSet(pEmailAddress, query);
		toXml(dd);
		
	}
*/
	
	public String getverifcationtoken(String useremail) throws JSchException, IOException,
	InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		String cpgdbhost = stack.getProperty("CPGDB");
		String vcode = null;
//		System.out.println("CPG DB_HOST" + cpgdbhost);
		String cpgDBurl = "jdbc:mysql://localhost:" + local_port + "/cpgDB";
		String app_sql="Select VerificationCode from RequestToken where User = '" + useremail + "' ORDER BY CreationTime DESC LIMIT 1";
		
		
		Statement stmt = null;
		String rhost = cpgdbhost;
		Connection conn = null;
		Session session = getSession(cpgdbhost, hostusername, ppkport, ppkfile);
		session.setPortForwardingL(local_port, rhost, mysql_port);
		Class.forName(driverName).newInstance();
		conn = (Connection) DriverManager.getConnection(cpgDBurl, dbuser,dbpwd);
		stmt = (Statement) conn.createStatement();
		ResultSet rs = stmt.executeQuery(app_sql);
		while (rs.next()) 
		{
			vcode = rs.getString(1);
		}
		conn.close();
		session.disconnect();
		
		return vcode;
		
	}
}