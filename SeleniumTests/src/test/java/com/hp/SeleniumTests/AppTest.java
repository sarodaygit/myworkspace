package com.hp.SeleniumTests;

import org.testng.TestNG;
import org.testng.annotations.Test;
import org.openqa.selenium.*;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleneseTestBase;
import com.thoughtworks.selenium.Selenium;

/**
 * Unit test for simple App.
 */
public class AppTest extends SeleneseTestBase

{
	@Test
	void f(){
		Selenium selenium = new DefaultSelenium("localhost", 4444, "C:\\Program Files (x86)\\Mozilla Firefox", "http://www.google.com");
		selenium.start();
	}
	
	}
	
