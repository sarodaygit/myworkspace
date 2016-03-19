/**
 * 
 */
package com.hp.SeleniumTests;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * @author sarodays
 *
 */
public class ChromeWebdrivertut {
	private WebDriver driver;
	private static ChromeDriverService service;
	
	@BeforeClass
	public void setup() throws IOException{
		
		
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\sarodays\\AppData\\Local\\Google\\Chrome\\Application\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS);
		
	}
	
	@Test
	public void chrometest() {
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
        Assert.assertEquals(true, true);     
        
        
	}
	
	@AfterClass
	public void close() {
		driver.quit();
		
	}
	
}
