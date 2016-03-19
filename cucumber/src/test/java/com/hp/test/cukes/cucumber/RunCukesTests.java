package com.hp.test.cukes.cucumber;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/resources/com/hp/test/cukes/cucumber",
		glue = "com.hp.test.cukes.cucumber.steps", 
		monochrome = true
		)
public class RunCukesTests {

}
