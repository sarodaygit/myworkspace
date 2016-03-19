package com.hp.automation.fleet.onramp.ngdc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class MultiThreadJobSubmit {

	static Logger log = Logger.getLogger(MultiThreadJobSubmit.class.getName());

	public static void main(String[] args) throws FileNotFoundException,
			IOException, InterruptedException {

		if (args == null || args.length < 2) {
			throw new RuntimeException(
					"\nUsage :-  Java -jar OnRampJobSubmit.jar <stack properties file> <Data file>\n"
							+ "\nExample :- Java -jar OnRampJobSubmit.jar dev02.properties DataSet.xlsx  \n");
		}

		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");

//		System.setProperty("https.protocols", "TLSv1");
		ExcelOperations exc = new ExcelOperations();

		String stackdetails = args[0];
		String stackname = stackdetails.split("\\.")[0];

		String pstorefile = "printer.properties";
		String pcredentialfile = "printerid.properties";
		String datafile = args[1];
		DBConnector dbc = new DBConnector(stackdetails);
		OAuthGen ogen = new OAuthGen(stackdetails, pcredentialfile);
		String auth = null, pEmailAddress = null, authtype = null, useremail = null;

		PrintWriter out;
		int day, month, year;
		int second, minute, hour;
		ArrayList<String> plist = new ArrayList<String>();
		List sheetdata = exc.readexceldata(datafile, Integer.valueOf(0));
		ExecutorService executor = Executors.newFixedThreadPool(5);

		String currDate = getCurrentDate();

		
		
		log.info("Retrieving Testcases information for Execution");
		log.info("Intiated TestCaseses Execution Cycle");
		for (int i = 1; i < sheetdata.size(); i++) {
			List testcase_row = (List) sheetdata.get(i);
			pEmailAddress = String.valueOf(testcase_row.get(8));
			authtype = String.valueOf(testcase_row.get(10));
			useremail = String.valueOf(testcase_row.get(11));
			log.info("Generating Authorization Tokens for "+pEmailAddress);
			auth = ogen.getAuth(stackdetails, pstorefile, pEmailAddress,
					authtype, useremail);
			log.info("Authorization Token retrieved for "+pEmailAddress);
			Runnable worker = new JobExecutor(stackdetails, datafile,
					pstorefile, testcase_row, auth);
			executor.execute(worker);

			if (!plist.contains(pEmailAddress))
				plist.add(pEmailAddress);

		}
		executor.shutdown();
		/*
		 * while (!executor.isTerminated()) { // System.out.println(""); }
		 */

		if (executor.awaitTermination(15, TimeUnit.MINUTES))
			System.out.println("Finished all threads");

		for (int j = 0; j < plist.size(); j++) {
			String pname = plist.get(j);
			System.out.println("Printer details = " + pname);
			String filename = pname + ".xml";
			String filename1 = pname + ".html";
			String shurl = dbc.getsharddetails(pname);
			dbc.getJobResults(shurl, pname, filename);
			dbc.getJobResultsHTML(shurl, pname, filename1);
		}

		
	}

	public static String getCurrentDate() {

		Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());

		Date currentTime = localCalendar.getTime();
		int currentDay = localCalendar.get(Calendar.DATE);
		int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
		int currentYear = localCalendar.get(Calendar.YEAR);
		int currentDayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK);
		int currentDayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH);
		int CurrentDayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR);

		/*
		 * System.out.println("Current Date and time details in local timezone");
		 * System.out.println("Current Date: " + currentTime);
		 * System.out.println("Current Day: " + currentDay);
		 * System.out.println("Current Month: " + currentMonth);
		 * System.out.println("Current Year: " + currentYear);
		 * System.out.println("Current Day of Week: " + currentDayOfWeek);
		 * System.out.println("Current Day of Month: " + currentDayOfMonth);
		 * System.out.println("Current Day of Year: " + CurrentDayOfYear);
		 */
		return currentDay + "-" + currentMonth + "-" + currentYear;

	}

}