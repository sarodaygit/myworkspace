package com.hp.automation.fleet.onramp.ngdc;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelOperations
{
  public List readexceldata(String filename, Integer page_num)
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
        data.add(cell);
      }

      sheetData.add(data);
    }
    return sheetData;
  }

  public void showexceldata(List p_sheetdata)
  {
    for (int i = 0; i < p_sheetdata.size(); i++)
    {
      List testcase_row = (List)p_sheetdata.get(i);
      String Test_ID = String.valueOf(testcase_row.get(0));
      String Test_Name = String.valueOf(testcase_row.get(1));
      String Test_Description = String.valueOf(testcase_row.get(2));
      String Request_XML = String.valueOf(testcase_row.get(3));
      String Test_Path = String.valueOf(testcase_row.get(4));
      String Filename = String.valueOf(testcase_row.get(5));
      String Doc_Type = String.valueOf(testcase_row.get(6));
      System.out.println(Test_ID + Test_Name + Test_Description + Request_XML + Test_Path + Filename + Doc_Type);
    }
  }
}