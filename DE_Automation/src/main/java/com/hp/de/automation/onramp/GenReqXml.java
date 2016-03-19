package com.hp.de.automation.onramp;

import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class GenReqXml {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String xcl = "req.xlsx";
		getreqxml(xcl);

	}

	private static void getreqxml(String p_datafile) {
		
		ExcelOperations exc = new ExcelOperations();
		List sheetdata = exc.readexceldata(p_datafile, Integer.valueOf(0));
		
		for (int i = 1; i < sheetdata.size(); i++)
		{
			List testcase_row = (List)sheetdata.get(i);

			String testType = String.valueOf(testcase_row.get(0));
			String testName = String.valueOf(testcase_row.get(2));
		}
		
	}

}
