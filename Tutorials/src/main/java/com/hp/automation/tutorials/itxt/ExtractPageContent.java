package com.hp.automation.tutorials.itxt;



import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

	import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;



	 
	public class ExtractPageContent {
	 
	    /** The original PDF that will be parsed. */
	    public static final String master_base_pdf_path = "/Master/pdf/";
	    public static final String master_base_text_path = "/Master/text/";
	    public static final String output_base_pdf_path = "/Output/pdf/";
	    public static final String output_base_text_path = "/Output/text/";
	   
	    

	    
	    public boolean parserectPdflocale(String pdf_path, String text_path) throws IOException {
	        PdfReader reader = new PdfReader(pdf_path);
	      
	        PrintWriter out = new PrintWriter(text_path,"UTF-8");
	        Rectangle rect = new Rectangle(70, 80, 490, 580);
	        RenderFilter filter = new RegionTextRenderFilter(rect);
	        TextExtractionStrategy strategy;
	        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
	        	
	            strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter);
	            
	            String extline = PdfTextExtractor.getTextFromPage(reader, i, strategy);
	            extline = extline.trim();
	            if(extline.length() > 0)
	            out.println(extline);
	            System.out.println(extline.length());
	        }
	        out.flush();
	        out.close();
	        reader.close();
	        
	        return out.checkError();
	    }
	 
	 
	    public static void main(String[] args) throws IOException {
	    	String encoding = "ko";
	    	
	    	String master_pdf_path =  "C:/Temp/Master/pdf/"+encoding+".pdf";
	    	System.out.println(master_pdf_path);
	    	
	    	String master_text_path =  "C:/Temp/Master/text/"+encoding+".txt";
	    	System.out.println(master_text_path);
	    	ExtractPageContent extract_data = new ExtractPageContent();
	    	Boolean complete = extract_data.parserectPdflocale(master_pdf_path, master_text_path);
	    	
	    	
	    	System.out.println(complete);
//	    	new ExtractPageContent().parserectPdflocale(master_pdf_path, master_text_path);
	    }
	}

