package com.hp.SeleniumTests;

import java.util.List;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public  class MyReporter implements IReporter{

	



	public void generateReport(XmlSuite suite, XmlTest test, String arg2) {
		// TODO Auto-generated method stub
		suite.getName();
		
		
	}

@Override
public void generateReport(List<XmlSuite> suite, List<ISuite> test, String arg2) {
	// TODO Auto-generated method stub

}

	

}
