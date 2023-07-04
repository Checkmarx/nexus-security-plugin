package com.checkmarx.plugins.nexus.capability;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import org.sonatype.nexus.capability.CapabilitySupport;

import static com.checkmarx.plugins.nexus.capability.checkmarxSecurityCapabilityKey.API_TOKEN;

@Named(checkmarxSecurityCapabilityDescriptor.CAPABILITY_ID)
public class checkmarxSecurityCapability extends CapabilitySupport<checkmarxSecurityCapabilityConfiguration> {

  @Inject
  public checkmarxSecurityCapability() {
  }

  @Override
  protected checkmarxSecurityCapabilityConfiguration createConfig(Map<String, String> properties) {
    return new checkmarxSecurityCapabilityConfiguration(properties);
  }

  @Override
  public boolean isPasswordProperty(String propertyName) {
    return API_TOKEN.propertyKey().equals(propertyName);
  }

  @Override
  protected void configure(checkmarxSecurityCapabilityConfiguration config) throws Exception {
    super.configure(config);
  }
}
