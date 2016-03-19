package com.hp.test.cukes.cucumber.steps;

import com.hp.test.cukes.cucumber.Subscriptions;

import junit.framework.Assert;
import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SubscribeSteps {

	private String message;
	private Subscriptions subscriptions;

	@Given("^I want to subscribe to receive important market information$")
	public void i_want_to_subscribe_to_receive_important_market_information()
			throws Throwable {
		subscriptions = new Subscriptions();

	}

	@When("^I enter a valid address (.*)$")
	public void i_enter_a_valid_address(String address) throws Throwable {
		message = subscriptions.subscribe(address);
	}

	@SuppressWarnings("deprecation")
	@Then("^should I get a welcome message$")
	public void should_I_get_a_welcome_message() throws Throwable {
		Assert.assertTrue("The welcome message should contain Welcome and it contained <" +
                message + ">", message.contains("Welcome"));
	}

	@When("^I enter an invalid address (.*)$")
	public void i_enter_an_invalid_address(String address) throws Throwable {
		message = subscriptions.subscribe(address);
	}

	@SuppressWarnings("deprecation")
	@Then("^should I get an error message$")
	public void should_I_get_an_error_message() throws Throwable {
		Assert.assertTrue("That the error message contain 'error. It contained <" +
                message + ">", message.contains("error"));
	}

}
