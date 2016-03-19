package com.hp.automation.fleet.onramp.ngdc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/*import com.jcraft.jsch.JSch;
 import com.jcraft.jsch.JSchException;
 import com.jcraft.jsch.Session;
 */
public class DBConnector {

	static Logger log = Logger.getLogger(DBConnector.class.getName());

	Properties config, stack, sshdetails;

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
		/*
		 * dbuser = "eprint"; // dbuser = "root"; // -ueprint
		 * -pXRfSUTD8fa9P6yRYwEfb dbpwd = "XRfSUTD8fa9P6yRYwEfb"; // dbpwd =
		 * "opelin";
		 */
		dbuser = stack.getProperty("DB_USER");
		dbpwd = stack.getProperty("DB_PASSWORD");
		log.info("login as db user :- "+dbuser);
	}

	public String getsharddetails(String pemailaddress) {
		Connection conn = null;
		Statement stmt = null;

		ResultSet rs;
		String app_sql = " Select s.jdbcUrl from Shard s, Device d where d.code = s.shardId and d.deviceEmailId = '"
				+ pemailaddress + "'";
		dbhost = stack.getProperty("CPGDB");
		cpgDBurl = "jdbc:mysql://" + dbhost + ":3306/cpgDB";
		String gshardurl = null;
		try {
			Class.forName(driverName).newInstance();
			conn = (Connection) DriverManager.getConnection(cpgDBurl, dbuser,
					dbpwd);
			stmt = (Statement) conn.createStatement();
			rs = stmt.executeQuery(app_sql);
			while (rs.next()) {
				gshardurl = rs.getString(1);
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

		log.info("Shard details of printer email address" + gshardurl);
		return gshardurl;

	}

	public String getPrinterFamily(String shardurl, String pEmailAddress) {

		String pcd = null;

		String app_sql = "Select ModelNumber,MakeAndModel from Printer where printerEmailAddress = '"
				+ pEmailAddress + "'";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = (Connection) DriverManager.getConnection(shardurl, dbuser,
					dbpwd);
			stmt = (Statement) conn.createStatement();
			rs = stmt.executeQuery(app_sql);
			while (rs.next()) {
				pcd = rs.getString(1) + "," + rs.getString(2);

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
		// return options;

	}

	public String getCredentials(String shardurl, String pEmailAddress) {

		String pcd = null;
		String app_sql = "select PrinterId, PrinterKey from Printer where  PrinterEmailAddress = '"
				+ pEmailAddress + "'";
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;

		try {
			Class.forName(driverName).newInstance();
			conn = (Connection) DriverManager.getConnection(shardurl, dbuser,
					dbpwd);
			stmt = (Statement) conn.createStatement();
			rs = stmt.executeQuery(app_sql);

			while (rs.next()) {
				pcd = rs.getString(1) + "," + rs.getString(2);

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

	public String getverifcationtoken(String useremail) throws IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException {
		String vcode = null;
		// log.info("CPG DB_HOST" + cpgdbhost);
		String app_sql = "Select VerificationCode from RequestToken where User = '"
				+ useremail + "' ORDER BY CreationTime DESC LIMIT 1";
		Statement stmt = null;
		Connection conn = null;
		Class.forName(driverName).newInstance();
		conn = (Connection) DriverManager
				.getConnection(cpgDBurl, dbuser, dbpwd);
		stmt = (Statement) conn.createStatement();
		ResultSet rs = stmt.executeQuery(app_sql);
		while (rs.next()) {
			vcode = rs.getString(1);
		}
		conn.close();

		return vcode;

	}

	public void getJobResults(String shardurl, String pname, String filename) {
		// TODO Auto-generated method stub

		String pcd = null;

		String app_sql = " SELECT d.jobId AS JOBID, d.id AS DOCID, d.NAME AS DOCNAME, d.createdAt AS DOCCREATIONTIME,fl.jobFlowLetState AS DOCPRINTSTATE, d.docformat AS DOCFORMAT,  j.NAME FROM Document d   LEFT JOIN JobFlowLet fl ON fl.jobFlowLetId = SUBSTRING(d.jobFlowLetId, 4, 99)  "
				+ " JOIN Job j ON d.jobId = j.id   JOIN Printer p ON p.InternalPrinterID = j.printerId WHERE   p.printeremailaddress IN ('"
				+ pname + "')   ORDER BY d.`createdAt` DESC";
		Statement stmt = null;
		Connection conn = null;

		try {
			Class.forName(driverName).newInstance();
			conn = (Connection) DriverManager.getConnection(shardurl, dbuser,
					dbpwd);
			stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery(app_sql);
			// Document xsd = RS2DOM.ResultSet2XSDDOM(rs);
			Document d = RS2DOM.ResultSet2DOM(rs);
			toXml(d, filename);

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

		// toXsd(xsd);

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return options;

	}

	public void getJobResultsHTML(String shardurl, String pname, String filename) {
		// TODO Auto-generated method stub

		String htmldata = null;

		String app_sql = "SELECT d.jobId AS JOBID, j.NAME AS JOBNAME, d.id AS DOCID, d.NAME AS DOCNAME, d.createdAt AS DOCCREATIONTIME,fl.jobFlowLetState AS DOCPRINTSTATE, d.docformat AS DOCFORMAT   FROM Document d   LEFT JOIN JobFlowLet fl ON fl.jobFlowLetId = SUBSTRING(d.jobFlowLetId, 4, 99)  "
				+ " JOIN Job j ON d.jobId = j.id   JOIN Printer p ON p.InternalPrinterID = j.printerId WHERE   p.printeremailaddress IN ('"
				+ pname + "')   ORDER BY d.`createdAt` DESC";
		Statement stmt = null;
		Connection conn = null;

		try {
			Class.forName(driverName).newInstance();
			conn = (Connection) DriverManager.getConnection(shardurl, dbuser,
					dbpwd);
			stmt = (Statement) conn.createStatement();
			ResultSet rs = stmt.executeQuery(app_sql);

			htmldata = toHTML(rs, filename);

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

		// toXsd(xsd);

		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return options;

	}

	private void toXml(Document d, String fname)
			throws TransformerFactoryConfigurationError {
		String fpath = null;
		try {
			Transformer myTransformer = (TransformerFactory.newInstance())
					.newTransformer();
			log.info("\n\nXML document containing the result set = " + fname);
			// myTransformer.transform(new DOMSource(d),new StreamResult(new
			// FileOutputStream(FileDescriptor.out)));
			File f = new File(fname);
			myTransformer.transform(new DOMSource(d), new StreamResult(f));
			fpath = f.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void toXsd(Document xsd)
			throws TransformerFactoryConfigurationError {
		String fpath = null;
		try {
			Transformer myTransformer = (TransformerFactory.newInstance())
					.newTransformer();

			// myTransformer.transform(new DOMSource(xsd), new StreamResult(new
			// FileOutputStream(FileDescriptor.out)));
			File f = new File("C:\\test.xsd");
			myTransformer.transform(new DOMSource(xsd), new StreamResult(f));
			fpath = f.getAbsolutePath();
			System.out
					.println("XML Schema instance describing the document test.xsd");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String toHTML(ResultSet rs, String filename) {
		String table_header = "<tr>";
		String resultdata = "";
		String final_data = "";

		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				table_header = table_header + "<th>" + rsmd.getColumnLabel(i)
						+ "</th>";
			}

			table_header = table_header + "</tr>";

			while (rs.isLast() == false) {
				rs.next();
				resultdata = resultdata + "<tr>";
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					resultdata = resultdata + "<td>" + rs.getString(i)
							+ "</td>";
				}
				resultdata = resultdata + "</tr>";
			}

			final_data = "<html><head><style>table,th,td{border:1px solid black;border-collapse:collapse;}</style></head><body><table style='width:300px'>"
					+ table_header + resultdata + "</table></body></html>";

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info(final_data);

		File f = new File("results/" + filename);
		File resultf = new File("results/results.html");

		try {
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			FileWriter rfw = new FileWriter(resultf.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			BufferedWriter rbw = new BufferedWriter(rfw);
			bw.write(final_data);
			rbw.write(final_data);
			bw.close();
			rbw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return final_data;

	}

}
