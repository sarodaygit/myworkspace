package com.hp.automation.tutorials.excel;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
public class ExcelOperations {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*List data_list = readexceldata("C:/Test.xlsx", 0);
		showexceldata(data_list);*/
		readdatacellbycell("C:/Test.xlsx", 0);
	}

	private static void readdatacellbycell(String filename, int i) {
		
		// TODO Auto-generated method stub
		 
		    FileInputStream fis = null;
		    XSSFWorkbook workbook = null;
		    try {
				fis = new FileInputStream(filename);
				workbook = new XSSFWorkbook(fis);
				XSSFSheet sheet = workbook.getSheetAt(i);
				Iterator rows = sheet.rowIterator();
				
			
				
				while (rows.hasNext()) {
				      XSSFRow row = (XSSFRow)rows.next();
				      if(row.getRowNum()==0){
				    	  continue;
				      }
				     System.out.println("\nrow number = " + row.getRowNum());
				     System.out.println("by row object = "+row.getCell(0) + "\t"+ row.getCell(1));
				     row.createCell(2).setCellValue("passed");
				    }
				  FileOutputStream fileOut = new FileOutputStream(filename);
			        workbook.write(fileOut);
			        fileOut.close();	
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	/*public static List readexceldata(String filename, Integer page_num)
	  {
	    List sheetData = new ArrayList();
	    FileInputStream fis = null;
	    try {
	      fis = new FileInputStream(filename);
	    }
	    catch (FileNotFoundException e)
	    {
	      e.printStackTrace();
	    }
	    XSSFWorkbook workbook = null;
	    try {
	      workbook = new XSSFWorkbook(fis);
	    }
	    catch (IOException e) {
	      e.printStackTrace();
	    }
	    XSSFSheet sheet = workbook.getSheetAt(page_num.intValue());

	    Iterator rows = sheet.rowIterator();
	    while (rows.hasNext()) {
	      XSSFRow row = (XSSFRow)rows.next();

	      Iterator cells = row.iterator();
	      
	      List data = new ArrayList();

	      while (cells.hasNext()) {
	        XSSFCell cell = (XSSFCell)cells.next();
//	        cell.setCellValue("Passed");
	        data.add(cell);
	      }

	      sheetData.add(data);
	    }
	    return sheetData;
	  }

	  public static void showexceldata(List p_sheetdata)
	  {
	    for (int i = 0; i < p_sheetdata.size(); i++)
	    {
	      List testcase_row = (List)p_sheetdata.get(i);
	      String Test_ID = String.valueOf(testcase_row.get(0));
	      String Test_Name = String.valueOf(testcase_row.get(1));
	      
	      System.out.println(Test_ID + Test_Name );
	    }
	  }
*/
}
