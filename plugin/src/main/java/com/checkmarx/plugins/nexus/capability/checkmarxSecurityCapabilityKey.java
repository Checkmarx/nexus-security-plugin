package com.checkmarx.plugins.nexus.capability;

public enum checkmarxSecurityCapabilityKey {
  API_URL("checkmarx.api.url", "https://api.dusti.co/v1/"),
  API_TOKEN("checkmarx.api.token",  ""),
  API_TRUST_ALL_CERTIFICATES("checkmarx.api.trust.all.certificates", "false"),
  ORGANIZATION_ID("checkmarx.organization.id", ""),
  RISKS_THRESHOLD("checkmarx.scanner.risks.threshold", "true"),
  PROXY_HOST("checkmarx.proxy.host", ""),
  PROXY_PORT("checkmarx.proxy.port", ""),
  PROXY_USER("checkmarx.proxy.user", ""),
  PROXY_PASSWORD("checkmarx.proxy.password", "");

  private final String propertyKey;
  private final String defaultValue;

  checkmarxSecurityCapabilityKey(String propertyKey, String defaultValue) {
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
