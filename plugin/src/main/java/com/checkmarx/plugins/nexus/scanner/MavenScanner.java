package com.checkmarx.plugins.nexus.scanner;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.checkmarx.sdk.util.PackageType;
import com.checkmarx.sdk.util.Package;
import com.checkmarx.plugins.nexus.model.ScanResult;
import com.checkmarx.plugins.nexus.util.Formatter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.checkmarx.sdk.api.v1.CheckmarxClient;
import com.checkmarx.sdk.model.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.common.collect.NestedAttributesMap;
import org.sonatype.nexus.repository.maven.MavenPath;
import org.sonatype.nexus.repository.maven.MavenPathParser;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.AssetStore;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Payload;
import retrofit2.Response;

import static com.checkmarx.plugins.nexus.util.Formatter.getRisksAsFormattedString;
import static com.checkmarx.plugins.nexus.capability.checkmarxSecurityCapabilityKey.API_TOKEN;


@Named
public class MavenScanner {
	private static final Logger LOG = LoggerFactory.getLogger(MavenScanner.class);

	private final AssetStore assetStore;
	private final MavenPathParser mavenPathParser;

	@Inject
	public MavenScanner(final AssetStore assetStore, final MavenPathParser mavenPathParser) {
		this.assetStore = assetStore;
		this.mavenPathParser = mavenPathParser;
	}

	public ScanResult scan(@Nonnull Context context, Payload payload, CheckmarxClient CheckmarxClient) {
		LOG.info("Maven - Scanning {}", context.getRequest().getPath());
		Object mavenPathAttribute = context.getAttributes().get(MavenPath.class.getName());
		if (!(mavenPathAttribute instanceof MavenPath)) {
			LOG.warn("Could not extract maven path from {}", context.getRequest().getPath());
			return null;
		}

		MavenPath mavenPath = (MavenPath) mavenPathAttribute;
		MavenPath parsedMavenPath = mavenPathParser.parsePath(mavenPath.getPath());
		MavenPath.Coordinates coordinates = parsedMavenPath.getCoordinates();
		if (coordinates == null) {
			LOG.warn("Coordinates are null for {}", parsedMavenPath);
			return null;
		}

		ScanResult scanResult = new ScanResult();

		if (checkmarxPropertiesExist(payload)) {
			LOG.info("Maven artifact {} was already scanned. Skip scanning", parsedMavenPath);

			NestedAttributesMap checkmarxSecurityMap = getCheckmarxSecurityAttributes(payload);
			Object risks = checkmarxSecurityMap.get("risks");
			if (risks instanceof String) {
				getRisksAsFormattedString(scanResult);
			}
		} else {
			try {
				LOG.info("Scanning maven artifact {}", parsedMavenPath);
				List<TestResult> testResult = null;
				String PackageName = coordinates. getGroupId() + ":" + coordinates.getArtifactId();
				String PackageVersion = coordinates.getVersion();
				Package mavenPackage = new Package(PackageType.MAVEN.getType(), PackageName, PackageVersion);
				ArrayList<Package> packages = new ArrayList<>();
				packages.add(mavenPackage);
				Response<List<TestResult>> response = CheckmarxClient.analyzePackage(packages, API_TOKEN.propertyKey()).execute();
				if (response.isSuccessful() && response.body() != null) {
					testResult = response.body();
					String responseAsText = new ObjectMapper().writeValueAsString(response.body());
					LOG.info("testMaven response: {}", responseAsText);
				}

				int risksCount = 0;
				List<String> risksList = new ArrayList<>();

				if (testResult != null) {
					updateAssetAttributes(testResult, coordinates, payload);
					for (TestResult result : testResult) {
						risksCount += result.risks.size();
						for (Map<String, String> risk : result.risks) {
							risksList.add(risk.get("title"));
						}
					}
					scanResult.risksCount = risksCount;
					scanResult.risksType = risksList;
				}

			} catch (IOException ex) {
				LOG.error("Could not test maven artifact: {}", parsedMavenPath, ex);
			}
		}
		LOG.info("Maven - Scan result: {}", scanResult);
		return scanResult;
	}

	private boolean checkmarxPropertiesExist(Payload payload) {
		NestedAttributesMap checkmarxSecurityMap = getCheckmarxSecurityAttributes(payload);
		if (checkmarxSecurityMap == null || checkmarxSecurityMap.isEmpty()) {
			return false;
		}
		Object risks = checkmarxSecurityMap.get("risks");
		return risks instanceof String && !((String) risks).isEmpty();
	}

	private void updateAssetAttributes(@Nonnull List<TestResult> testResult, @Nonnull MavenPath.Coordinates coordinates, Payload payload) {
		if (payload instanceof Content) {
			Asset asset = ((Content) payload).getAttributes().get(Asset.class);
			if (asset == null) {
				return;
			}

			NestedAttributesMap checkmarxSecurityMap = asset.attributes().child("checkmarx Security");
			checkmarxSecurityMap.clear();

			checkmarxSecurityMap.set("risks", testResult);

			assetStore.save(asset);
		}
	}

	private NestedAttributesMap getCheckmarxSecurityAttributes(Payload payload) {
		if (!(payload instanceof Content)) {
			return null;
		}
		Asset asset = ((Content) payload).getAttributes().get(Asset.class);
		if (asset == null) {
			return null;
		}

		return asset.attributes().child("checkmarx Security");
	}
}
