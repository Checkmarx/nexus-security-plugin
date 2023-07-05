package com.checkmarx.plugins.nexus.capability;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import org.sonatype.nexus.capability.CapabilitySupport;

import static com.checkmarx.plugins.nexus.capability.CheckmarxSecurityCapabilityKey.API_TOKEN;

@Named(CheckmarxSecurityCapabilityDescriptor.CAPABILITY_ID)
public class CheckmarxSecurityCapability extends CapabilitySupport<CheckmarxSecurityCapabilityConfiguration> {

  @Inject
  public CheckmarxSecurityCapability() {
  }

  @Override
  protected CheckmarxSecurityCapabilityConfiguration createConfig(Map<String, String> properties) {
    return new CheckmarxSecurityCapabilityConfiguration(properties);
  }

  @Override
  public boolean isPasswordProperty(String propertyName) {
    return API_TOKEN.propertyKey().equals(propertyName);
  }

  @Override
  protected void configure(CheckmarxSecurityCapabilityConfiguration config) throws Exception {
    super.configure(config);
  }
}
