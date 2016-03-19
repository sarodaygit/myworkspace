package com.hp.test.cukes.cucumber;

import java.util.HashSet;
import java.util.Set;

public class Subscriptions {
	private Set<String> subscribers = new HashSet<String>();

	public String subscribe(String address) {
		if (validateEmailAddress(address)) {
			subscribers.add(address);
			return "Welcome " + address;
		} else {
			return "There was an error subscribing";
		}
	}

	public String unsubscribe(String address) {
		if (isSubscribing(address)) {
			subscribers.remove(address);
			return "Goodbye " + address;
		} else {
			return "not a subscriber " + address;
		}
	}

	public boolean isSubscribing(String address) {
		return subscribers.contains(address);
	}

	private boolean validateEmailAddress(String address) {
		return address.contains("@");
	}

}
