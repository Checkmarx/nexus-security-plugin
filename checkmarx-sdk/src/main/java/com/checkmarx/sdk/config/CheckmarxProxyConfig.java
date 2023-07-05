package com.checkmarx.sdk.config;

public class CheckmarxProxyConfig {
  private final String proxyHost;
  private final int proxyPort;
  private final String proxyUser;
  private final String proxyPassword;

  public CheckmarxProxyConfig(String proxyHost, int proxyPort, String proxyUser, String proxyPassword) {
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;
    this.proxyUser = proxyUser;
    this.proxyPassword = proxyPassword;
  }

  public String getProxyHost() {
    return proxyHost;
  }

  public int getProxyPort() {
    return proxyPort;
  }

  public String getProxyUser() {
    return proxyUser;
  }

  public String getProxyPassword() {
    return proxyPassword;
  }
}
