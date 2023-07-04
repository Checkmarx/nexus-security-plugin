package com.checkmarx.sdk.util;

public enum PackageType {
	MAVEN("mvn"),
	NPM("npm"),
	PYPI("pypi"),
	NUGET("nuget"),
	RUBYGEMS("rubygems");

	private final String type;

	PackageType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
