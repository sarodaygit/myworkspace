package com.hp.automation.fleet.onramp.ngdc;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom.Namespace;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;;

public class sampledate {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		/*Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
	      
        Date currentTime = localCalendar.getTime();
        int currentDay = localCalendar.get(Calendar.DATE);
        int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
        int currentYear = localCalendar.get(Calendar.YEAR);
        int currentDayOfWeek = localCalendar.get(Calendar.DAY_OF_WEEK);
        int currentDayOfMonth = localCalendar.get(Calendar.DAY_OF_MONTH);
        int CurrentDayOfYear = localCalendar.get(Calendar.DAY_OF_YEAR);

        System.out.println("Current Date and time details in local timezone");
        System.out.println("Current Date: " + currentTime);
        System.out.println("Current Day: " + currentDay);
        System.out.println("Current Month: " + currentMonth);
        System.out.println("Current Year: " + currentYear);
        System.out.println("Current Day of Week: " + currentDayOfWeek);
        System.out.println("Current Day of Month: " + currentDayOfMonth);
        System.out.println("Current Day of Year: " + CurrentDayOfYear);
*/

		/*String stackdetails = "test2.properties";
		String[] tmp = stackdetails.split("\\.");
		System.out.println(tmp[0]);*/

		  ExcelOperations exc = new ExcelOperations();
		  String execution_state = null;
		 
		  String datafile = args[0];
		  List sheetdata = exc.readexceldata(datafile, Integer.valueOf(0));
		  String rqxml = null;
      
       for (int i = 1; i < sheetdata.size(); i++) {
       	List testcase_row = (List)sheetdata.get(i);
			 execution_state = String.valueOf(testcase_row.get(9));
			 if(execution_state.equalsIgnoreCase("yes")){
//				 System.out.println("\n"+String.valueOf(testcase_row.get(2)) + "\t"+String.valueOf(testcase_row.get(4)));
				rqxml = String.valueOf(testcase_row.get(4));

				rqxml = rqxml.replaceFirst("#printerid#", "nagaraj.saroday").replaceFirst("#testname#", "TESTONE");
				System.out.println(rqxml);
				/*Document doc = StringToDocument(rqxml);
				updateNodeValue(doc,"!!!!!!!","");
				String newxml = DocumentToString(doc);*/
//				System.out.println(newxml);
			 }
			
       }
			
   
       }
	
	
	
		
	

}
