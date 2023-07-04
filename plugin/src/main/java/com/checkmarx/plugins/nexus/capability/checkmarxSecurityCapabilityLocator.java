package com.checkmarx.plugins.nexus.capability;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.capability.Capability;
import org.sonatype.nexus.capability.CapabilityReference;
import org.sonatype.nexus.capability.CapabilityRegistry;

@Named
public class checkmarxSecurityCapabilityLocator {
  private static final Logger LOG = LoggerFactory.getLogger(checkmarxSecurityCapabilityLocator.class);

  private final CapabilityRegistry capabilityRegistry;

  @Inject
  public checkmarxSecurityCapabilityLocator(final CapabilityRegistry capabilityRegistry) {
    this.capabilityRegistry = capabilityRegistry;
  }

  public boolean isCheckmarxSecurityCapabilityActive() {
    LOG.debug("List all available Nexus capabilities");
    CapabilityReference reference = capabilityRegistry.getAll().stream()
                                                      .peek(e -> {
                                                        Capability capability = e.capability();
                                                        LOG.debug("  {}", capability);
                                                      })
                                                      .filter(e -> checkmarxSecurityCapability.class.getSimpleName().equals(e.capability().getClass().getSimpleName()))
                                                      .findFirst().orElse(null);
    if (reference == null) {
      LOG.debug("checkmarx Security Configuration capability does not exist.");
      return false;
    }

    return reference.context().isActive();
  }

  public checkmarxSecurityCapabilityConfiguration getCheckmarxSecurityCapabilityConfiguration() {
    CapabilityReference reference = capabilityRegistry.getAll().stream()
                                                      .filter(e -> checkmarxSecurityCapability.class.getSimpleName().equals(e.capability().getClass().getSimpleName()))
                                                      .findFirst().orElse(null);
    if (reference == null) {
      LOG.debug("checkmarx Security Configuration capability not created.");
      return null;
    }

    checkmarxSecurityCapability checkmarxSecurityCapability = reference.capabilityAs(checkmarxSecurityCapability.class);
    return checkmarxSecurityCapability.getConfig();
  }
}
