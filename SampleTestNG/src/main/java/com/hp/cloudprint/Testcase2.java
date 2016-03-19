package com.hp.cloudprint;
import org.testng.Assert;
import org.testng.annotations.*;
import java.util.List;


public class Testcase2  {

  @Test
   public void Method1() {
	  System.out.println("Testcase2 -> Method1 is executed");

	  Assert.assertEquals(true, false);
  }



}