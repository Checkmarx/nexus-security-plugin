package com.checkmarx.plugins.nexus.capability;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sonatype.nexus.capability.CapabilityDescriptorSupport;
import org.sonatype.nexus.capability.CapabilityType;
import org.sonatype.nexus.capability.Tag;
import org.sonatype.nexus.capability.Taggable;
import org.sonatype.nexus.formfields.CheckboxFormField;
import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.StringTextFormField;
import org.sonatype.nexus.formfields.PasswordFormField;

@Singleton
@Named(checkmarxSecurityCapabilityDescriptor.CAPABILITY_ID)
public class checkmarxSecurityCapabilityDescriptor extends CapabilityDescriptorSupport<checkmarxSecurityCapabilityConfiguration> implements Taggable {
  static final String CAPABILITY_ID = "checkmarx.security";
  private static final String CAPABILITY_NAME = "checkmarx Security Configuration";
  private static final String CAPABILITY_DESCRIPTION = "Provides support to test artifacts against the checkmarx risks database";

  private final StringTextFormField fieldApiUrl;
  private final StringTextFormField fieldApiToken;
  private final CheckboxFormField fieldUseCustomSSLCertificate;
  private final StringTextFormField fieldProxyHost;
  private final StringTextFormField fieldProxyPort;
  private final StringTextFormField fieldProxyUser;
  private final PasswordFormField fieldProxyPassword;

  public checkmarxSecurityCapabilityDescriptor() {
    fieldApiUrl = new StringTextFormField(checkmarxSecurityCapabilityKey.API_URL.propertyKey(), "checkmarx API URL", "", FormField.MANDATORY).withInitialValue(checkmarxSecurityCapabilityKey.API_URL.defaultValue());
    fieldApiToken = new StringTextFormField(checkmarxSecurityCapabilityKey.API_TOKEN.propertyKey(), "checkmarx API Token", "", FormField.MANDATORY).withInitialValue(checkmarxSecurityCapabilityKey.API_TOKEN.defaultValue());
    fieldUseCustomSSLCertificate = new CheckboxFormField(checkmarxSecurityCapabilityKey.API_TRUST_ALL_CERTIFICATES.propertyKey(), "Use custom SSL certificate", "", FormField.OPTIONAL).withInitialValue(false);
    fieldProxyHost = new StringTextFormField(checkmarxSecurityCapabilityKey.PROXY_HOST.propertyKey(), "Proxy host (optional)", "", FormField.OPTIONAL).withInitialValue(checkmarxSecurityCapabilityKey.PROXY_HOST.defaultValue());
    fieldProxyPort = new StringTextFormField(checkmarxSecurityCapabilityKey.PROXY_PORT.propertyKey(), "Proxy port (optional)", "", FormField.OPTIONAL).withInitialValue(checkmarxSecurityCapabilityKey.PROXY_PORT.defaultValue());
    fieldProxyUser = new StringTextFormField(checkmarxSecurityCapabilityKey.PROXY_USER.propertyKey(), "Proxy username (optional)", "", FormField.OPTIONAL).withInitialValue(checkmarxSecurityCapabilityKey.PROXY_USER.defaultValue());
    fieldProxyPassword = new PasswordFormField(checkmarxSecurityCapabilityKey.PROXY_PASSWORD.propertyKey(), "Proxy password (optional)", "", FormField.OPTIONAL).withInitialValue(checkmarxSecurityCapabilityKey.PROXY_PASSWORD.defaultValue());
  }

  @Override
  public CapabilityType type() {
    return CapabilityType.capabilityType(CAPABILITY_ID);
  }

  @Override
  public String name() {
    return CAPABILITY_NAME;
  }

  @Override
  public String about() {
    return CAPABILITY_DESCRIPTION;
  }

  @Override
  public List<FormField> formFields() {
    return Arrays.asList(fieldApiUrl, fieldApiToken, fieldUseCustomSSLCertificate, fieldProxyHost, fieldProxyPort, fieldProxyUser, fieldProxyPassword);
  }

  @Override
  protected checkmarxSecurityCapabilityConfiguration createConfig(Map<String, String> properties) {
    return new checkmarxSecurityCapabilityConfiguration(properties);
  }

  @Override
  public Set<Tag> getTags() {
    return Collections.singleton(Tag.categoryTag("Security"));
  }
}
