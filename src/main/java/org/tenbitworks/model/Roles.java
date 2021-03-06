package org.tenbitworks.model;

public enum Roles {
	USER("User"),
	ADMIN("Admin"),
	API("Api"),
	ACTUATOR("Metrics");
	
	private final String text;

	private Roles(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}
}
