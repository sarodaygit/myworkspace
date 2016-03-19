package com.hp.cloudprint;
import org.testng.Assert;
import org.testng.annotations.*;
import java.util.List;


public class Testcase1  {

  @Test
   public void Method1() {
	  System.out.println("Testcase1 -> Method1 is executed");

	  Assert.assertEquals(true, true);
  }



}