package com.hp.cloudprint;

import java.util.List;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.Reporter;
import org.testng.xml.XmlSuite;

public class MyReporter implements IReporter{

	@Override
	public void generateReport(List<XmlSuite> arg0, List<ISuite> arg1,
			String arg2) {
		Reporter.log("<p>Reporter Listener is CalledT</p>");
		// TODO Auto-generated method stub
	
		
	}

}
