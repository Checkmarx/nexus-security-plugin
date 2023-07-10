package com.checkmarx.plugins.nexus.capability;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.capability.Capability;
import org.sonatype.nexus.capability.CapabilityReference;
import org.sonatype.nexus.capability.CapabilityRegistry;

@Named
public class CheckmarxSecurityCapabilityLocator {
  private static final Logger LOG = LoggerFactory.getLogger(CheckmarxSecurityCapabilityLocator.class);

  private final CapabilityRegistry capabilityRegistry;

  @Inject
  public CheckmarxSecurityCapabilityLocator(final CapabilityRegistry capabilityRegistry) {
    this.capabilityRegistry = capabilityRegistry;
  }

  public boolean isCheckmarxSecurityCapabilityActive() {
    LOG.debug("List all available Nexus capabilities");
    CapabilityReference reference = capabilityRegistry.getAll().stream()
                                                      .peek(e -> {
                                                        Capability capability = e.capability();
                                                        LOG.debug("  {}", capability);
                                                      })
                                                      .filter(e -> CheckmarxSecurityCapability.class.getSimpleName().equals(e.capability().getClass().getSimpleName()))
                                                      .findFirst().orElse(null);
    if (reference == null) {
      LOG.debug("Checkmarx Security Configuration capability does not exist.");
      return false;
    }

    return reference.context().isActive();
  }

  public CheckmarxSecurityCapabilityConfiguration getCheckmarxSecurityCapabilityConfiguration() {
    CapabilityReference reference = capabilityRegistry.getAll().stream()
                                                      .filter(e -> CheckmarxSecurityCapability.class.getSimpleName().equals(e.capability().getClass().getSimpleName()))
                                                      .findFirst().orElse(null);
    if (reference == null) {
      LOG.debug("Checkmarx Security Configuration capability not created.");
      return null;
    }

    CheckmarxSecurityCapability checkmarxSecurityCapability = reference.capabilityAs(CheckmarxSecurityCapability.class);
    return checkmarxSecurityCapability.getConfig();
  }
}
