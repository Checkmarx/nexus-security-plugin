package com.checkmarx.plugins.nexus.scanner;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.checkmarx.plugins.nexus.model.ScanResult;
import com.checkmarx.plugins.nexus.util.Formatter;
import com.checkmarx.plugins.nexus.capability.checkmarxSecurityCapabilityConfiguration;
import com.checkmarx.sdk.api.v1.CheckmarxClient;
import jline.internal.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.types.ProxyType;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Payload;
import org.sonatype.nexus.repository.view.Response;
import org.sonatype.nexus.repository.view.handlers.ContributedHandler;

import static java.lang.String.format;

@Named
@Singleton
public class ScannerHandler implements ContributedHandler {
	private static final Logger LOG = LoggerFactory.getLogger(ScannerHandler.class);

	private final ConfigurationHelper configurationHelper;
	public final MavenScanner mavenScanner;
	private final NpmScanner npmScanner;
	private final PypiScanner pypiScanner;
	private final RubygemsScanner rubygemsScanner;
	private final NugetScanner nugetScanner;

	private CheckmarxClient CheckmarxClient;
	private checkmarxSecurityCapabilityConfiguration configuration;

	@Inject
	public ScannerHandler(final ConfigurationHelper configurationHelper,
						  final MavenScanner mavenScanner,
						  final NpmScanner npmScanner,
						  final PypiScanner pypiScanner,
						  final RubygemsScanner rubygemsScanner,
						  final NugetScanner nugetScanner) {
		LOG.info("ScannerHandler constructor");
		this.configurationHelper = configurationHelper;
		this.mavenScanner = mavenScanner;
		this.npmScanner = npmScanner;
		this.pypiScanner = pypiScanner;
		this.rubygemsScanner = rubygemsScanner;
		this.nugetScanner = nugetScanner;
		initializeModuleIfNeeded();
	}

	@Nonnull
	@Override
	public Response handle(@Nonnull Context context) throws Exception {
		Response response = context.proceed();
		if (!configurationHelper.isCapabilityEnabled()) {
			LOG.debug("checkmarxSecurityCapability is not enabled.");
			return response;
		}

		Repository repository = context.getRepository();
		LOG.info("repository: {}, {}", repository.getName(), repository.getType());

		Payload payload = response.getPayload();
		ScanResult scanResult = null;
		String repositoryFormat = repository.getFormat().getValue();
		switch (repositoryFormat) {
			case "maven2": {
				LOG.info("scanning maven2 repository");
				scanResult = mavenScanner.scan(context, payload, CheckmarxClient);
				LOG.info("scanResult: {}", scanResult);
				LOG.info("scanResult Count: {}", scanResult.risksCount);
				LOG.info("scanResult Type: {}", scanResult.risksType);
				break;
			}
			case "npm": {
				LOG.info("scanning npm repository");
				scanResult = npmScanner.scan(context, payload, CheckmarxClient);
				LOG.info("scanResult: {}", scanResult);
				LOG.info("scanResult Count: {}", scanResult.risksCount);
				LOG.info("scanResult Type: {}", scanResult.risksType);
				break;
			}
			case "pypi": {
				LOG.info("scanning pypi repository");
				scanResult = pypiScanner.scan(context, payload, CheckmarxClient);
				LOG.info("scanResult Count: {}", scanResult.risksCount);
				LOG.info("scanResult Type: {}", scanResult.risksType);
				break;
			}
			case "rubygems": {
				LOG.info("scanning rubygems repository");
				scanResult = rubygemsScanner.scan(context, payload, CheckmarxClient);
				LOG.info("scanResult: {}", scanResult);
				LOG.info("scanResult Count: {}", scanResult.risksCount);
				LOG.info("scanResult Type: {}", scanResult.risksType);
				Log.info("scanResult: {}");
				break;
			}
			case "nuget": {
				LOG.info("scanning nuget repository");
				scanResult = nugetScanner.scan(context, payload, CheckmarxClient);
				LOG.info("scanResult: {}", scanResult);
				LOG.info("scanResult Count: {}", scanResult.risksCount);
				LOG.info("scanResult Type: {}", scanResult.risksType);
				break;
			}
			default:
				LOG.error("format {} is not supported", repositoryFormat);
				LOG.info("scanResult: {}", scanResult);
				LOG.info("scanResult Count: {}", scanResult.risksCount);
				LOG.info("scanResult Type: {}", scanResult.risksType);
				return response;
		}

		validateRisks(scanResult, context.getRequest().getPath());
		return response;
	}


	public void initializeModuleIfNeeded() {
		if (CheckmarxClient == null) {
			CheckmarxClient = configurationHelper.getCheckmarxClient();
		}
		if (configuration == null) {
			configuration = configurationHelper.getConfiguration();
		}
	}

	private void validateRisks(ScanResult scanResult, @Nonnull String path) {
		if (scanResult == null) {
			LOG.warn("Component could not be scanned, check the logs scanner modules: {}", path);
		}

	}

}
