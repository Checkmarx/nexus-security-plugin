package com.checkmarx.sdk.util;


public class Package {
	private String type;
	private String name;
	private String version;

	public Package(String type, String name, String version) {
		this.type = type;
		this.name = name;
		this.version = version;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "{\"name\": \"" + name + "\", \"type\": \"" + type + "\", \"version\": \"" + version + "\"}";
	}
}

