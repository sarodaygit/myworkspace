package com.hp.SeleniumTests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.openqa.*;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestBase;
import com.thoughtworks.selenium.SeleneseTestCase;
import com.thoughtworks.selenium.SeleneseTestNgHelper;
import com.thoughtworks.selenium.Selenium;


@SuppressWarnings("deprecation")
public class NewTest extends SeleneseTestNgHelper {
	
	
	@Test
	 public void setUp() throws Exception {
		setUp("www.google.co.in","*chrome");
      }

	

      public void testNew() throws Exception {
           selenium.open("/");
           selenium.type("q", "Cheese");
           selenium.waitForPageToLoad("40000");
           
           
//           selenium.click("btnG");
//           selenium.waitForPageToLoad("30000");
           selenium.close();
           assertTrue(selenium.isTextPresent("Results * for selenium rc"));
           // These are the real test steps
           
      }
}
