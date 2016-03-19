package com.hp.SeleniumTests;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;


public class RunListener implements ITestListener{

	public void onFinish(ITestContext arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStart(ITestContext arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onTestFailure(ITestResult arg0) {
		// TODO Auto-generated method stub+ 
		System.out.println("THE FOLLOWING TEST FAILED" +arg0.getTestClass()+ " = " +arg0.FAILURE );
		
	}

	public void onTestSkipped(ITestResult arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onTestStart(ITestResult arg0) {
		
		System.out.println("TEST EXECUTION STARTED FOR CLASS = "+arg0.getTestClass());
		// TODO Auto-generated method stub
		
	}

	public void onTestSuccess(ITestResult arg0) {
		
		// TODO Auto-generated method stub
		
	}

	
	

}
