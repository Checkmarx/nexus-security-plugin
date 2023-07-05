package com.checkmarx.plugins.nexus.scanner;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.checkmarx.plugins.nexus.capability.CheckmarxSecurityCapabilityLocator;
import com.checkmarx.plugins.nexus.capability.CheckmarxSecurityCapabilityConfiguration;
import com.checkmarx.sdk.Checkmarx;
import com.checkmarx.sdk.api.v1.CheckmarxClient;
import com.checkmarx.sdk.api.v1.PackageResponse;
import com.checkmarx.sdk.config.CheckmarxProxyConfig;
import com.checkmarx.sdk.model.NotificationSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Named
@Singleton
public class ConfigurationHelper {
  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationHelper.class);

  @Inject
  private Provider<CheckmarxSecurityCapabilityLocator> locatorProvider;

  @Nullable
  public CheckmarxClient getCheckmarxClient() {
    CheckmarxSecurityCapabilityLocator locator = locatorProvider.get();

    if (locator == null) {
      LOG.warn("CheckmarxClient cannot be built because CheckmarxSecurityCapabilityLocator is null!");
      return null;
    } else {
      try {
        String proxyHost = locator.getCheckmarxSecurityCapabilityConfiguration().getProxyHost();
        String proxyPort = locator.getCheckmarxSecurityCapabilityConfiguration().getProxyPort();
        String proxyUser = locator.getCheckmarxSecurityCapabilityConfiguration().getProxyUser();
        String proxyPassword = locator.getCheckmarxSecurityCapabilityConfiguration().getProxyPassword();
        if(!proxyHost.isEmpty() && !proxyPort.isEmpty()) {
            CheckmarxProxyConfig checkmarxProxyConfig = new CheckmarxProxyConfig(proxyHost, Integer.parseInt(proxyPort), proxyUser, proxyPassword);
            return Checkmarx.newBuilder(new Checkmarx.Config(locator.getCheckmarxSecurityCapabilityConfiguration().getApiToken(), checkmarxProxyConfig)).buildSync();
        }
        return Checkmarx.newBuilder(new Checkmarx.Config(locator.getCheckmarxSecurityCapabilityConfiguration().getApiToken())).buildSync();
      } catch (Exception ex) {
        LOG.error("CheckmarxClient could not be created", ex);
        return null;
      }
    }
  }

  @Nullable
  public CheckmarxSecurityCapabilityConfiguration getConfiguration() {
    CheckmarxSecurityCapabilityLocator locator = locatorProvider.get();

    if (locator == null) {
      return null;
    } else {
      return locator.getCheckmarxSecurityCapabilityConfiguration();
    }
  }

  public boolean isCapabilityEnabled() {
    CheckmarxSecurityCapabilityLocator locator = locatorProvider.get();

    if (locator == null) {
      return false;
    }

    return locator.isCheckmarxSecurityCapabilityActive();
  }

	public NotificationSettings getNotificationSettings() throws IOException {
		return null;
	}

	public PackageResponse testConnection() throws IOException {
		return null;

	}

	public void testAndPersistConfiguration(CheckmarxSecurityCapabilityConfiguration configuration) throws IOException {
	}

	public void testAndPersistNotificationSettings(NotificationSettings notificationSettings) throws IOException {

	}

	public void testAndPersistConfiguration(CheckmarxSecurityCapabilityConfiguration configuration, NotificationSettings notificationSettings) throws IOException {

	}

	public void testAndPersistConfiguration(CheckmarxSecurityCapabilityConfiguration configuration, NotificationSettings notificationSettings, boolean testNotification) throws IOException {

	}
}
