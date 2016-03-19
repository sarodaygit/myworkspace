package com.hp.cloudprint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.testng.Reporter;
import org.testng.TestNG;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class RunTest  {
  @Test
  public void TestRunner() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
			
			//invoke testcases
			XmlSuite suite = new XmlSuite();
	    	suite.setName("JEFTestSuite");
	    	XmlTest test = new XmlTest(suite);
	    	test.setName("JEFTestSets");
	    	List<XmlClass> classes = new ArrayList<XmlClass>();
	    	MyReporter repo = new MyReporter();
	    	NodeList testList = ParseXml();
	    	String reportHtml = "<h4>JEF TEST SUITE REPORTS</h4>";
	    	Reporter.log(reportHtml);	
	    	for (int temp = 0; temp < testList.getLength(); temp++) {
			
	    		Node nNode = testList.item(temp);
	    			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
 
	    				Element eElement = (Element) nNode;
	    				String TName = getTagValue("name", eElement);
	    				String RName = getTagValue("run", eElement);
			      
	    					if(  RName.contentEquals("yes"))
	    						{
	    							classes.add(new XmlClass(TName));
	    						}
			    
  
	    			}
	    	}
			   
	    	test.setXmlClasses(classes) ;
	    	List<XmlSuite> suites = new ArrayList<XmlSuite>();
	    	suites.add(suite);
	    	TestNG tng = new TestNG();
	    	tng.addListener(new RunListener());
	    	tng.setXmlSuites(suites);
	    	tng.addListener(repo);
//	    	repo.generateReport(suite, test, "myouput");
	    	tng.run();
	    	

  }

//@Test
  public NodeList ParseXml() throws ParserConfigurationException, SAXException, IOException {
		
		File fXmlFile = new File("TestSuite.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		
//		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		NodeList nList = doc.getElementsByTagName("Test");
//		System.out.println("-----------------------");
		
		return nList;
		
	}
	 
	  private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	 
	        Node nValue = (Node) nlList.item(0);
	 
		return nValue.getNodeValue();
	  }



}
