package com.checkmarx.plugins.nexus.capability;

import java.util.Map;
import org.sonatype.nexus.capability.CapabilityConfigurationSupport;

public class checkmarxSecurityCapabilityConfiguration extends CapabilityConfigurationSupport {
  private final String apiUrl;
  private final String apiToken;
  private final String proxyHost;
  private final String proxyPort;
  private final String proxyUser;
  private final String proxyPassword;

  checkmarxSecurityCapabilityConfiguration(Map<String, String> properties) {
    apiUrl = properties.getOrDefault(checkmarxSecurityCapabilityKey.API_URL.propertyKey(), checkmarxSecurityCapabilityKey.API_URL.defaultValue());
    apiToken = properties.get(checkmarxSecurityCapabilityKey.API_TOKEN.propertyKey());
    proxyHost = properties.getOrDefault(checkmarxSecurityCapabilityKey.PROXY_HOST.propertyKey(), checkmarxSecurityCapabilityKey.PROXY_HOST.defaultValue());
    proxyPort = properties.getOrDefault(checkmarxSecurityCapabilityKey.PROXY_PORT.propertyKey(), checkmarxSecurityCapabilityKey.PROXY_PORT.defaultValue());
    proxyUser = properties.getOrDefault(checkmarxSecurityCapabilityKey.PROXY_USER.propertyKey(), checkmarxSecurityCapabilityKey.PROXY_USER.defaultValue());
    proxyPassword = properties.getOrDefault(checkmarxSecurityCapabilityKey.PROXY_PASSWORD.propertyKey(), checkmarxSecurityCapabilityKey.PROXY_PASSWORD.defaultValue());

  }

  public String getApiUrl() {
    return apiUrl;
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
