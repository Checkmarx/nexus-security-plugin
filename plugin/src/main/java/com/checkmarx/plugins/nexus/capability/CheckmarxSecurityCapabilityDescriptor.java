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
@Named(CheckmarxSecurityCapabilityDescriptor.CAPABILITY_ID)
public class CheckmarxSecurityCapabilityDescriptor extends CapabilityDescriptorSupport<CheckmarxSecurityCapabilityConfiguration> implements Taggable {
	static final String CAPABILITY_ID = "checkmarx.security";
	private static final String CAPABILITY_NAME = "Checkmarx Supply Chain Security Threat Intelligence";
	private static final String CAPABILITY_DESCRIPTION = "Provides support to test artifacts against the Checkmarx risks database";

	private final StringTextFormField fieldApiToken;
	private final CheckboxFormField fieldUseCustomSSLCertificate;
	private final StringTextFormField fieldProxyHost;
	private final StringTextFormField fieldProxyPort;
	private final StringTextFormField fieldProxyUser;
	private final PasswordFormField fieldProxyPassword;

	public CheckmarxSecurityCapabilityDescriptor() {
		fieldApiToken = new StringTextFormField(CheckmarxSecurityCapabilityKey.API_TOKEN.propertyKey(), "Checkmarx SCS Threat Intelligence API Token", "", FormField.MANDATORY).withInitialValue(CheckmarxSecurityCapabilityKey.API_TOKEN.defaultValue());
		fieldUseCustomSSLCertificate = new CheckboxFormField(CheckmarxSecurityCapabilityKey.API_TRUST_ALL_CERTIFICATES.propertyKey(), "Use custom SSL certificate", "", FormField.OPTIONAL).withInitialValue(false);
		fieldProxyHost = new StringTextFormField(CheckmarxSecurityCapabilityKey.PROXY_HOST.propertyKey(), "Proxy host (optional)", "", FormField.OPTIONAL).withInitialValue(CheckmarxSecurityCapabilityKey.PROXY_HOST.defaultValue());
		fieldProxyPort = new StringTextFormField(CheckmarxSecurityCapabilityKey.PROXY_PORT.propertyKey(), "Proxy port (optional)", "", FormField.OPTIONAL).withInitialValue(CheckmarxSecurityCapabilityKey.PROXY_PORT.defaultValue());
		fieldProxyUser = new StringTextFormField(CheckmarxSecurityCapabilityKey.PROXY_USER.propertyKey(), "Proxy username (optional)", "", FormField.OPTIONAL).withInitialValue(CheckmarxSecurityCapabilityKey.PROXY_USER.defaultValue());
		fieldProxyPassword = new PasswordFormField(CheckmarxSecurityCapabilityKey.PROXY_PASSWORD.propertyKey(), "Proxy password (optional)", "", FormField.OPTIONAL).withInitialValue(CheckmarxSecurityCapabilityKey.PROXY_PASSWORD.defaultValue());
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
		return Arrays.asList(fieldApiToken, fieldUseCustomSSLCertificate, fieldProxyHost, fieldProxyPort, fieldProxyUser, fieldProxyPassword);
	}

	@Override
	protected CheckmarxSecurityCapabilityConfiguration createConfig(Map<String, String> properties) {
		return new CheckmarxSecurityCapabilityConfiguration(properties);
	}

	@Override
	public Set<Tag> getTags() {
		return Collections.singleton(Tag.categoryTag("Security"));
	}
}
