package com.checkmarx.plugins.nexus.capability;

import com.checkmarx.sdk.Checkmarx;

public enum CheckmarxSecurityCapabilityKey {
	API_TOKEN("checkmarx.api.token", ""),
	API_TRUST_ALL_CERTIFICATES("checkmarx.api.trust.all.certificates", "false"),
	PROXY_HOST("checkmarx.proxy.host", ""),
	PROXY_PORT("checkmarx.proxy.port", ""),
	PROXY_USER("checkmarx.proxy.user", ""),
	PROXY_PASSWORD("checkmarx.proxy.password", "");

	private final String propertyKey;
	private final String defaultValue;

	CheckmarxSecurityCapabilityKey(String propertyKey, String defaultValue) {
		this.propertyKey = propertyKey;
		this.defaultValue = defaultValue;
	}

	public String propertyKey() {
		return propertyKey;
	}

	public String defaultValue() {
		return defaultValue;
	}
}
