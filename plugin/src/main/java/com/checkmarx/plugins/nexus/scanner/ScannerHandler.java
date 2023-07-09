package com.checkmarx.plugins.nexus.scanner;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.checkmarx.plugins.nexus.model.ScanResult;
import com.checkmarx.plugins.nexus.capability.CheckmarxSecurityCapabilityConfiguration;
import com.checkmarx.sdk.api.v1.CheckmarxClient;
import com.checkmarx.sdk.api.v1.PackageRequest;
import com.checkmarx.sdk.api.v1.PackageResponse;
import com.checkmarx.sdk.util.PackageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.common.collect.NestedAttributesMap;
import org.sonatype.nexus.repository.Repository;
import org.sonatype.nexus.repository.Type;
import org.sonatype.nexus.repository.maven.MavenPath;
import org.sonatype.nexus.repository.maven.MavenPathParser;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Payload;
import org.sonatype.nexus.repository.view.Response;
import org.sonatype.nexus.repository.view.handlers.ContributedHandler;
import org.sonatype.nexus.repository.view.Status;

import retrofit2.Call;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named
@Singleton
public class ScannerHandler implements ContributedHandler {
	private static final Logger LOG = LoggerFactory.getLogger(ScannerHandler.class);

	private final ConfigurationHelper configurationHelper;
	private final MavenPathParser mavenPathParser;

	private CheckmarxClient checkmarxClient;
	private CheckmarxSecurityCapabilityConfiguration configuration;

	@Inject
	public ScannerHandler(final ConfigurationHelper configurationHelper, final MavenPathParser mavenPathParser) {
		LOG.info("ScannerHandler constructor");
		this.configurationHelper = configurationHelper;
		this.mavenPathParser = mavenPathParser;
		initializeModuleIfNeeded();
	}


	private static ScanResult scanPackage(CheckmarxClient checkmarxClient, PackageRequest packageRequest) throws IOException {
		ScanResult scanResult = new ScanResult();
		List<PackageResponse> packageResponse;
		ArrayList<PackageRequest> packageRequests = new ArrayList<>();
		packageRequests.add(packageRequest);
		Call<List<PackageResponse>> call = checkmarxClient.analyzePackage(packageRequests);
		retrofit2.Response<List<PackageResponse>> response = call.execute();
		packageResponse = response.body();

		if (!response.isSuccessful() || packageResponse == null) {
			throw new RuntimeException(); // TODO exception
		}

		for (PackageResponse result : packageResponse) {
			for (Map<String, String> risk : result.risks) {
				String riskTitle = risk.get("title");
				scanResult.addRisk(riskTitle);
			}
		}
		LOG.info(MessageFormat.format("scanned package \"{0}/{1}\" with {2} risks", packageRequest.getType(), packageRequest.getName(), scanResult.getRisksCount()));
		return scanResult;
	}


	private static class PackageTypeNotSupportedException extends Exception {

		public PackageTypeNotSupportedException(String message) {
			super(message);
		}
	}


	@Nonnull
	@Override
	public Response handle(@Nonnull Context context) throws Exception {
		Response response = context.proceed();

		LOG.info(MessageFormat.format("checking {0}", context.getRequest().getPath()));

		if (!configurationHelper.isCapabilityEnabled()) {
			LOG.warn("CheckmarxSecurityCapability is not enabled.");
			return response;
		}


		try {
			PackageRequest packageRequest = getPackageRequest(response, context);
			ScanResult scanResult = scanPackage(checkmarxClient, packageRequest);
			if (scanResult.getRisksCount() > 0) {
				String errorMessage = MessageFormat.format("Package download blocked by Checkmarx Supply Chain Security Plugin.\nPackage name: \"{0}/{1}\" contains {2} risks: {3}\n", packageRequest.getType(), packageRequest.getName(), scanResult.getRisksCount(), scanResult.getRisks());
				LOG.error(errorMessage);
				response = new Response.Builder()
					.status(new Status(false, 405, errorMessage))
					.payload(response.getPayload())
					.build();
				return response;
			}

			return response;
		} catch (PackageTypeNotSupportedException e) {
			LOG.warn("Package type not supported: {}", e.getMessage());
			return response;
		}
	}

	private PackageRequest getPackageRequest(Response response, Context context) throws PackageTypeNotSupportedException {
		String packageType = "";
		String packageName = "";
		String packageVersion = "";

		Repository repository = context.getRepository();
		String repositoryName = repository.getName();
		Type repositoryType = repository.getType();
		LOG.info("repository: {}, {}", repositoryName, repositoryType);

		Payload payload = response.getPayload();
		if (!(payload instanceof Content)) {
			throw new RuntimeException("could not parse response");
		}
		Asset asset = ((Content) payload).getAttributes().get(Asset.class);
		if (asset == null) {
			throw new RuntimeException("could not parse response");
		}

		PackageRequest packageRequest;
		String repositoryFormat = repository.getFormat().getValue();
		switch (repositoryFormat) {
			case "maven2": {
				Object mavenPathAttribute = context.getAttributes().get(MavenPath.class.getName());
				if (!(mavenPathAttribute instanceof MavenPath)) {
					throw new RuntimeException(MessageFormat.format("Could not extract maven path from {0}", context.getRequest().getPath()));
				}

				MavenPath mavenPath = (MavenPath) mavenPathAttribute;
				MavenPath parsedMavenPath = mavenPathParser.parsePath(mavenPath.getPath());
				MavenPath.Coordinates coordinates = parsedMavenPath.getCoordinates();
				if (coordinates == null) {
					throw new RuntimeException(MessageFormat.format("Coordinates are null for {0}", parsedMavenPath));
				}

				// TODO -> set in package name type version the values
				break;
			}
			case "npm": {
				packageType = PackageType.NPM.getType();
				NestedAttributesMap pypiAttributes;
				pypiAttributes = asset.attributes().child(packageType);
				Object nameAttribute = pypiAttributes.get("name");
				packageName = nameAttribute != null ? nameAttribute.toString() : "";
				Object versionAttribute = pypiAttributes.get("version");
				packageVersion = versionAttribute != null ? versionAttribute.toString() : "";
				break;
			}
			case "pypi": {
				packageType = PackageType.PYPI.getType();
				NestedAttributesMap pypiAttributes;
				pypiAttributes = asset.attributes().child(packageType);
				Object nameAttribute = pypiAttributes.get("name");
				packageName = nameAttribute != null ? nameAttribute.toString() : "";
				Object versionAttribute = pypiAttributes.get("version");
				packageVersion = versionAttribute != null ? versionAttribute.toString() : "";
				break;
			}
			case "rubygems": {
				packageType = PackageType.RUBYGEMS.getType();
				NestedAttributesMap pypiAttributes;
				pypiAttributes = asset.attributes().child(packageType);
				Object nameAttribute = pypiAttributes.get("name");
				packageName = nameAttribute != null ? nameAttribute.toString() : "";
				Object versionAttribute = pypiAttributes.get("version");
				packageVersion = versionAttribute != null ? versionAttribute.toString() : "";
				break;
			}
			case "nuget": {
				packageType = PackageType.NUGET.getType();
				NestedAttributesMap pypiAttributes = asset.attributes().child(packageType);
				Object nameAttribute = pypiAttributes.get("name");
				packageName = nameAttribute != null ? nameAttribute.toString() : "";
				Object versionAttribute = pypiAttributes.get("version");
				packageVersion = versionAttribute != null ? versionAttribute.toString() : "";
				break;
			}
			default:
				throw new PackageTypeNotSupportedException(MessageFormat.format("package type \"{0}\" not supported", repositoryFormat));
		}

		packageRequest = new PackageRequest(packageType, packageName, packageVersion);
		return packageRequest;
	}


	public void initializeModuleIfNeeded() {
		if (checkmarxClient == null) {
			checkmarxClient = configurationHelper.getCheckmarxClient();
		}
		if (configuration == null) {
			configuration = configurationHelper.getConfiguration();
		}
	}


}
