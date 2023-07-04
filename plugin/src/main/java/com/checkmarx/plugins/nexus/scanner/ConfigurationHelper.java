package com.checkmarx.plugins.nexus.scanner;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.checkmarx.plugins.nexus.capability.checkmarxSecurityCapabilityLocator;
import com.checkmarx.plugins.nexus.capability.checkmarxSecurityCapabilityConfiguration;
import com.checkmarx.sdk.checkmarx;
import com.checkmarx.sdk.api.v1.CheckmarxClient;
import com.checkmarx.sdk.config.checkmarxProxyConfig;
import com.checkmarx.sdk.model.NotificationSettings;
import com.checkmarx.sdk.model.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Named
@Singleton
public class ConfigurationHelper {
  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationHelper.class);

  @Inject
  private Provider<checkmarxSecurityCapabilityLocator> locatorProvider;

  @Nullable
  public CheckmarxClient getCheckmarxClient() {
    checkmarxSecurityCapabilityLocator locator = locatorProvider.get();

    if (locator == null) {
      LOG.warn("CheckmarxClient cannot be built because checkmarxSecurityCapabilityLocator is null!");
      return null;
    } else {
      try {
        String proxyHost = locator.getCheckmarxSecurityCapabilityConfiguration().getProxyHost();
        String proxyPort = locator.getCheckmarxSecurityCapabilityConfiguration().getProxyPort();
        String proxyUser = locator.getCheckmarxSecurityCapabilityConfiguration().getProxyUser();
        String proxyPassword = locator.getCheckmarxSecurityCapabilityConfiguration().getProxyPassword();
        if(!proxyHost.isEmpty() && !proxyPort.isEmpty()) {
            checkmarxProxyConfig checkmarxProxyConfig = new checkmarxProxyConfig(proxyHost, Integer.parseInt(proxyPort), proxyUser, proxyPassword);
            return checkmarx.newBuilder(new checkmarx.Config(locator.getCheckmarxSecurityCapabilityConfiguration().getApiToken(), checkmarxProxyConfig)).buildSync();
        }
        return checkmarx.newBuilder(new checkmarx.Config(locator.getCheckmarxSecurityCapabilityConfiguration().getApiToken())).buildSync();
      } catch (Exception ex) {
        LOG.error("CheckmarxClient could not be created", ex);
        return null;
      }
    }
  }

  @Nullable
  public checkmarxSecurityCapabilityConfiguration getConfiguration() {
    checkmarxSecurityCapabilityLocator locator = locatorProvider.get();

    if (locator == null) {
      return null;
    } else {
      return locator.getCheckmarxSecurityCapabilityConfiguration();
    }
  }

  public boolean isCapabilityEnabled() {
    checkmarxSecurityCapabilityLocator locator = locatorProvider.get();

    if (locator == null) {
      return false;
    }

    return locator.isCheckmarxSecurityCapabilityActive();
  }

	public NotificationSettings getNotificationSettings() throws IOException {
		return null;
	}

	public TestResult testConnection() throws IOException {
		return null;

	}

	public void testAndPersistConfiguration(checkmarxSecurityCapabilityConfiguration configuration) throws IOException {
	}

	public void testAndPersistNotificationSettings(NotificationSettings notificationSettings) throws IOException {

	}

	public void testAndPersistConfiguration(checkmarxSecurityCapabilityConfiguration configuration, NotificationSettings notificationSettings) throws IOException {

	}

	public void testAndPersistConfiguration(checkmarxSecurityCapabilityConfiguration configuration, NotificationSettings notificationSettings, boolean testNotification) throws IOException {

	}
}
