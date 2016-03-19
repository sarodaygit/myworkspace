package com.hp.automation.Fleet.localization.infosheet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class InfosheetAutomation {

	/**
	 * @param args
	 */
	
	static String prn_reg_url = "http://16.177.36.108/PSRestUtils/v/1.0/printer/register";
	static String tool_path = "C:/Fleet/Automation/Localization/L10N_I18N_Script/";
	static String test_file = tool_path+"Tests.xlsx";
	
	public static void main(String[] args) {
		String stack_name = args[0];
		
		
		runsuite(stack_name);

	}
	
	
	
private static void runsuite(String stack_name) {
		
	String infosheeturl = null, downloadpath = null, response = null,parsefilepath=null,d_path=null,masterp=null,masterpdfp=null,masterp_temp;
	String language_code = "en";
	String city_code = "unitedStates";
	
	
		 
		    FileInputStream fis = null;
		    XSSFWorkbook workbook = null;
		    try {
				fis = new FileInputStream(test_file);
				workbook = new XSSFWorkbook(fis);
				XSSFSheet sheet = workbook.getSheetAt(0);
				Iterator rows = sheet.rowIterator();
				
			
				
				while (rows.hasNext()) {
				      XSSFRow row = (XSSFRow)rows.next();
				      if(row.getRowNum()==0){
				    	  continue;
				      }
				      language_code = String.valueOf(row.getCell(0));
				      city_code = String.valueOf(row.getCell(1));
//				     System.out.println(language_code + "\t" + city_code);
				     
				     downloadpath = tool_path +"output/"+ language_code+".pdf";
				     parsefilepath = tool_path +"output/"+ language_code+"_temp.txt";
				     d_path = tool_path +"output/"+ language_code+".txt";
				     masterpdfp = tool_path +"master_files/"+language_code +".pdf";
				     masterp_temp = tool_path +"master_files/"+ language_code+"_temp.txt";
				     masterp = tool_path +"master_files/"+ language_code+".txt";
				     System.out.println("Stack selected = " + stack_name +"\n");
				     System.out.println("\nLanguage selected = " + language_code +"\tCountry Selected = " + city_code);
				     
//						Register a Printer
						response = registerPrinter(stack_name,language_code,city_code);
						
//						System.out.println("\nHttp ResponseBody = "+response);
//						Download the infosheet
						infosheeturl = getinfosheeturl(response);
						System.out.println("Infosheet download path = " + infosheeturl);
						int download_status = downloadFileData(infosheeturl, downloadpath);
						System.out.println("File download status = " + download_status);
						if(download_status==200 || download_status==201 || download_status==202) {
							System.out.println("File download successful = " + downloadpath);
				
//							extract text from infosheet pdf and store it in text file
							Boolean chk = parsePdf(masterpdfp,masterp_temp);
							 chk=parsePdf(downloadpath, parsefilepath);
							if(!chk){
								System.out.println("parsed successfully");
//							Format the text file			
								List<String> oplines=formatOutput(masterp_temp,masterp);
								oplines=formatOutput(parsefilepath,d_path);
//								Boolean cmp = filecompare(lang_code);
								
								
//								Compare the formatted text with master text file.
								Boolean compare_result =  outputcompare(masterp, d_path);

								if(compare_result){
									System.out.println("Language "+ language_code +" Test Passed");
									row.createCell(2).setCellValue("Passed");
								} else {
									System.out.println("Language "+ language_code +" Test Failed");
									row.createCell(2).setCellValue("Failed");
								}
							}
							
						} else {
							System.out.println("File Download failed");
						}
				     
				     
				     
				     System.out.println("\n@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@\n");
				     
				     
				     

				    }
				  FileOutputStream fileOut = new FileOutputStream(test_file);
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

private static int downloadFileData(String fileDataURL, String localDownloadPath) {
		// TODO Auto-generated method stub
		// makes the HTTP get request
        HttpClient client = new HttpClient();
        InputStream in;
        FileOutputStream out;
	
        GetMethod get = new GetMethod(fileDataURL);
   
        try {
			client.executeMethod(get);
			 in = get.getResponseBodyAsStream();
			 out = new FileOutputStream(new File(localDownloadPath));   
		        byte[] b = new byte[1024];
		        int len = 0;
		        while ((len = in.read(b)) != -1) {
		            out.write(b, 0, len);
		        }
		        in.close();
		        out.close();
			
			
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       

        // get HTTP response
        Integer statusCode = get.getStatusCode();
        Header[] headers = get.getResponseHeaders();

        return statusCode;
		
	}

	private static String registerPrinter(String p_stack_name,String p_lang_code,String p_country_code) {
		
		String responsebody = null;
		int response=0;
		
	
		
		PostMethod prnJobMethod = new PostMethod(prn_reg_url);

		prnJobMethod.addRequestHeader("Host", "16.177.36.108");
		prnJobMethod.addRequestHeader("Content-Type", "application/xml");
//		prnJobMethod.addRequestHeader("Content-Length", "1073");
	
		
		String requestbody = "<?xml version='1.0' encoding='UTF-8'?>"+
		"<registerPrinter>"+
		"<stack>"+p_stack_name+"</stack>"+
		"<modelName>HP PhotoSmart D110a</modelName>"+
		"<modelNumber>CE861A:HP LaserJet Thunderbird Series</modelNumber>"+
		"<duration>120</duration>"+
		"<breathtime>-1</breathtime>"+
		"<waitForInstructionPage>true</waitForInstructionPage>"+
		"<registerTime>4200000</registerTime>"+
		"<country>"+p_country_code+"</country>"+
		"<language>"+p_lang_code+"</language>"+
		"<owner>FLEET</owner>"+
		"<ownerObjective>SetYourObjective</ownerObjective>"+
		"<prefetchAuthCode>false</prefetchAuthCode>"+
		"<number>1</number>"+
		"<registerbatch>1</registerbatch>"+
		"<delaynextbatch>5.0</delaynextbatch>"+
		"<test>none</test>"+
		"<numjobs>1</numjobs>"+
		"<jobFiles>none</jobFiles>"+
		"<delaynextjob>60.0</delaynextjob>"+
		"<hpcUser></hpcUser>"+
		"<batchsizeperprinter>1</batchsizeperprinter>"+
		"<delaybetweentowjobsinbatchperprinter>0.0</delaybetweentowjobsinbatchperprinter>"+
		"<submitInSequence>true</submitInSequence>"+
		"<initialDelay>0.0</initialDelay>"+
		"<allowsSips>true</allowsSips>"+
		"<allowsMobileApps>true</allowsMobileApps>"+
		"<allowsEmail>true</allowsEmail>"+
		"<protectionMode>false</protectionMode>"+
		"<allowsUsageDataCollection>false</allowsUsageDataCollection>"+
		"</registerPrinter>";
		prnJobMethod.setRequestBody(requestbody);
		
//		System.out.println("Printer registration URL = " + prn_reg_url);
//		System.out.println("\nRequestBody = " + requestbody + "\n");
		
		// Create Httpclient
		HttpClient httpclient = new HttpClient();

		try {
			
			response = httpclient.executeMethod(prnJobMethod);
			System.out.println("Http response code = "+response);
			
			
			if(response == 200 || response == 201 || response == 202){
				responsebody = prnJobMethod.getResponseBodyAsString();
//				System.out.println("\nHttp ResponseBody = "+responsebody);
				
				
			
				
			} else {
				
				System.out.println("Printer registration request failed");
			}
			
			
			
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

		return responsebody;
	}

	private static String getinfosheeturl(String responsebody)
			 {
		SAXBuilder builder = new SAXBuilder();
		Reader reader;
		Document doc;
		String infosheeturl =null;
		reader = new StringReader(responsebody);
		try {
			doc = builder.build(reader);
			Element root = doc.getRootElement();
			Element prn_element = root.getChild("printer");
			
			
			infosheeturl = prn_element.getChildText("registrationPage");
			System.out.println("PrinterEmailAddress = " + prn_element.getChildText("printerEmailId"));
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("Infosheet URL = "+infosheeturl);
		return infosheeturl;
	}

	private static Boolean parsePdf(String pdf, String txt) throws IOException {
	        PdfReader reader = new PdfReader(pdf);
	        PrintWriter out = new PrintWriter(new FileOutputStream(txt));
	        Rectangle rect = new Rectangle(70, 80, 490, 750);
	        RenderFilter filter = new RegionTextRenderFilter(rect);
	        TextExtractionStrategy strategy;
	        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
	            strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter);
	            out.println(PdfTextExtractor.getTextFromPage(reader, i, strategy));
	        }
	        out.flush();
	        out.close();
	        
	        reader.close();
			return out.checkError();
	    }
	
	private static List<String> formatOutput(String fp, String d_path) throws FileNotFoundException,
	IOException {

			System.out.println();
			FileReader frd = new FileReader(fp); 
			BufferedReader br = new BufferedReader(frd); 
			
			PrintWriter printwriter = new PrintWriter(new File(d_path));
			String line;
			
			List<String> plocalestr = new CopyOnWriteArrayList<String>();
			int i =1;
			
			while((line = br.readLine()) != null )
			{ 
				line = line.trim();
				if(line.length() > 0){
					printwriter.println(line);
			//		System.out.println("line number = "+ i + " Line size = "+line.length() + " data = " + line);
					plocalestr.add(line);
				}
				i++;
			} 
			
			frd.close();
			printwriter.close();
			FileUtils.forceDelete(new File(fp));
			return plocalestr;
			}

	private static Boolean outputcompare(String fp, String d_path) throws FileNotFoundException,
	IOException {

			Boolean result = false;
			FileReader fr1 = new FileReader(fp);
			LineNumberReader lnr1 = new LineNumberReader(fr1);
			FileReader fr2 = new FileReader(d_path);
			LineNumberReader lnr2 = new LineNumberReader(fr2);
			String line1 = "";
			String line2 = "";
			
			while(line1!=null && line2!=null)
			{
				
				if(line1.equals(line2)){
					result = true;
				} else if(!line1.equals(line2) && (line1.split(":")[0].equals(line2.split(":")[0]))) {
					
						System.out.println("Master-Compare failed line = " + line1.split(":")[1] + "\tOutput-Compare failed line = "+ line2.split(":")[1]);
						result = true;
					 
						
				
				} else  {
					result = false;
								System.out.println("file compare failed = " + result);
								break;
				}
				
				line1 = lnr1.readLine();
				line2 = lnr2.readLine();
			}
			
			fr1.close();
			fr2.close();
				return result;
}

	

}
