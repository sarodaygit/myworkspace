package com.hp.automation.enums;

public enum ActionsEnums {
	STATUS(0),
	START(1),
	STOP(2);
	
	private int actionValue;
	
	ActionsEnums(int actionValue) {
		this.actionValue = actionValue;
	}

	public int getActionValue() {
		return actionValue;
	}

}
