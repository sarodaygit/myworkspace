package com.hp.automation.fleet.oauth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;



public class DBConnector {
	
	Properties config,stack,sshdetails;
	
	String dbuser;
	String dbpwd;
	String dbhost;
	String cpgDBurl;
	String driverName;

	// String shardurl;
	
	
	
	public DBConnector(String p_stackdetails) throws FileNotFoundException,
			IOException {

		stack = new Properties();

		stack.load(new FileInputStream(p_stackdetails));
		driverName = "com.mysql.jdbc.Driver";
		dbuser = "eprint";
		// -ueprint -pXRfSUTD8fa9P6yRYwEfb
		dbpwd = "XRfSUTD8fa9P6yRYwEfb";

	}
	
	public String getsharddetails(String pemailaddress) {
		Connection conn = null;
		Statement stmt = null;

		ResultSet rs;
		String app_sql = " Select s.jdbcUrl from Shard s, Device d where d.code = s.shardId and d.deviceEmailId = '"
				+ pemailaddress + "'";
		dbhost = stack.getProperty("CPGDB");
//		System.out.println("getting stack details = " + dbhost);
//		System.out.println("Reading username and password = " + dbuser + "   "+dbpwd);
//		System.out.println("Reading printer email address = " + pemailaddress);
		cpgDBurl = "jdbc:mysql://" + dbhost + ":3306/cpgDB";
		String shardurl = null;
		try {
			Class.forName(driverName).newInstance();
			conn = (Connection) DriverManager.getConnection(cpgDBurl, dbuser,
					dbpwd);
			stmt = (Statement) conn.createStatement();
			rs = stmt.executeQuery(app_sql);
			while (rs.next()) {
				shardurl = rs.getString(1);
			}

		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Reading printer email address");
		return shardurl;

	}
	
	public String getPrinterFamily(String shardurl,String pEmailAddress)  {
		
String pcd = null;
		
		
		String app_sql="Select ModelNumber,MakeAndModel from Printer where printerEmailAddress = '" +pEmailAddress+"'";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection(shardurl, dbuser,dbpwd);
			stmt = (Statement) conn.createStatement();
			rs = stmt.executeQuery(app_sql);
			while (rs.next()) 
			{
				pcd = rs.getString(1) + "," +rs.getString(2);
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pcd;
//		return options;

		
	}

	public String getCredentials( String shardurl,String pEmailAddress) {
		
		String pcd = null;
		String app_sql="select PrinterId, PrinterKey from Printer where  PrinterEmailAddress = '" +pEmailAddress+"'";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		
		try {
			Class.forName(driverName).newInstance();
			conn = (Connection) DriverManager.getConnection(shardurl, dbuser,dbpwd);
			stmt = (Statement) conn.createStatement();
			rs = stmt.executeQuery(app_sql);
			
			while (rs.next()) 
			{
				pcd = rs.getString(1) + "," +rs.getString(2);
				
			}
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
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return pcd;
		
	}
		
	public String getverifcationtoken(String useremail) throws  IOException,
	InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String vcode = null;
//		System.out.println("CPG DB_HOST" + cpgdbhost);
		String app_sql="Select VerificationCode from RequestToken where User = '" + useremail + "' ORDER BY CreationTime DESC LIMIT 1";
		Statement stmt = null;
		Connection conn = null;
		Class.forName(driverName).newInstance();
		conn = (Connection) DriverManager.getConnection(cpgDBurl, dbuser,dbpwd);
		stmt = (Statement) conn.createStatement();
		ResultSet rs = stmt.executeQuery(app_sql);
		while (rs.next()) 
		{
			vcode = rs.getString(1);
		}
		conn.close();
		
		
		return vcode;
		
	}

	public void getJobResults(String shardurl,String pname, String filename) {
		// TODO Auto-generated method stub
		
		String pcd = null;
		
		
		String app_sql="SELECT d.jobId, d.id, fl.jobFlowLetState, d.state, d.docformat, d.NAME AS DocumentName, j.NAME FROM Document d   LEFT JOIN JobFlowLet fl ON fl.jobFlowLetId = SUBSTRING(d.jobFlowLetId, 4, 99)  "
				+ " JOIN Job j ON d.jobId = j.id   JOIN Printer p ON p.InternalPrinterID = j.printerId WHERE   p.printeremailaddress IN ('" + pname +"')   ORDER BY d.`createdAt` DESC";
		Statement stmt = null;
		Connection conn = null;
		
		try {
			Class.forName(driverName).newInstance();
			conn = (Connection) DriverManager.getConnection(shardurl, dbuser,dbpwd);
			stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery(app_sql);
//			Document xsd = RS2DOM.ResultSet2XSDDOM(rs);
			Document d = RS2DOM.ResultSet2DOM(rs);
			toXml(d,filename);
			
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
		
//		toXsd(xsd);
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
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

}
