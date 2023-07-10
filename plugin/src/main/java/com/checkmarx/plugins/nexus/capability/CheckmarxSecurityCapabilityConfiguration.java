package com.checkmarx.plugins.nexus.capability;

import java.util.Map;
import org.sonatype.nexus.capability.CapabilityConfigurationSupport;

public class CheckmarxSecurityCapabilityConfiguration extends CapabilityConfigurationSupport {
  private final String apiToken;
  private final String proxyHost;
  private final String proxyPort;
  private final String proxyUser;
  private final String proxyPassword;

  CheckmarxSecurityCapabilityConfiguration(Map<String, String> properties) {
    apiToken = properties.get(CheckmarxSecurityCapabilityKey.API_TOKEN.propertyKey());
    proxyHost = properties.getOrDefault(CheckmarxSecurityCapabilityKey.PROXY_HOST.propertyKey(), CheckmarxSecurityCapabilityKey.PROXY_HOST.defaultValue());
    proxyPort = properties.getOrDefault(CheckmarxSecurityCapabilityKey.PROXY_PORT.propertyKey(), CheckmarxSecurityCapabilityKey.PROXY_PORT.defaultValue());
    proxyUser = properties.getOrDefault(CheckmarxSecurityCapabilityKey.PROXY_USER.propertyKey(), CheckmarxSecurityCapabilityKey.PROXY_USER.defaultValue());
    proxyPassword = properties.getOrDefault(CheckmarxSecurityCapabilityKey.PROXY_PASSWORD.propertyKey(), CheckmarxSecurityCapabilityKey.PROXY_PASSWORD.defaultValue());
  }


  public String getApiToken() {
    return apiToken;
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public String getProxyPort() {
    return proxyPort;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public String getProxyPassword() {
    return proxyPassword;
  }
}
