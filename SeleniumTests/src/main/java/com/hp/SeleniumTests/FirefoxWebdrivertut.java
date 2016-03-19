/**
 * 
 */
package com.hp.SeleniumTests;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;

/**
 * @author sarodays
 *
 */
public class FirefoxWebdrivertut {
	private WebDriver driver;
	
	@BeforeClass
	public void setup(){
		
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS);
//		driver1 = new ChromeDriver();
	}
	
	@Test
	public void firefoxtest() {
		driver.get("http://google.com/");
        System.out.println(driver.getTitle());
        WebElement query = driver.findElement(By.name("q"));
        query.sendKeys("Cheese");
        WebElement srchbtn = driver.findElement(By.id("gbqfb"));
        srchbtn.click();
//        driver.findElement(By.linkText("Cheese - GNOME Project Listing")).click();
        WebElement lnk = driver.findElement(By.linkText("Cheese - Wikipedia, the free encyclopedia"));
        System.out.println("clicked on link");
        lnk.click();
         Assert.assertEquals(true,false);   
        
        
	}
	
	@AfterClass
	public void close() {
		driver.close();
	}
	
}
