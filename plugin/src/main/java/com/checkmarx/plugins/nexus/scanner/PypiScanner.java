package com.checkmarx.plugins.nexus.scanner;

import com.checkmarx.sdk.util.PackageType;
import com.checkmarx.sdk.util.Package;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.checkmarx.plugins.nexus.model.ScanResult;
import com.checkmarx.sdk.api.v1.CheckmarxClient;
import com.checkmarx.sdk.model.TestResult;
import jline.internal.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.common.collect.NestedAttributesMap;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.AssetStore;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Payload;
import retrofit2.Call;
import retrofit2.Response;

import static com.checkmarx.plugins.nexus.capability.checkmarxSecurityCapabilityKey.API_TOKEN;
import static com.checkmarx.plugins.nexus.util.Formatter.getRisksAsFormattedString;
import static com.checkmarx.plugins.nexus.util.Formatter.getRisksCount;

@Named
public class PypiScanner {
	private static final Logger LOG = LoggerFactory.getLogger(PypiScanner.class);

	private final AssetStore assetStore;

	@Inject
	public PypiScanner(final AssetStore assetStore) {
		this.assetStore = assetStore;
	}

	public ScanResult scan(@Nonnull Context context, Payload payload, CheckmarxClient CheckmarxClient) {
		LOG.info("Scanning PYPI artifact {}", context.getRequest().getPath());
		if (payload == null) {
			return null;
		}

		String packageName = "";
		String packageVersion = "";
		if (payload instanceof Content) {
			Asset asset = ((Content) payload).getAttributes().get(Asset.class);
			if (asset == null) {
				return null;
			}

			NestedAttributesMap pypiAttributes = asset.attributes().child("pypi");
			Object nameAttribute = pypiAttributes.get("name");
			packageName = nameAttribute != null ? nameAttribute.toString() : "";
			Object versionAttribute = pypiAttributes.get("version");
			packageVersion = versionAttribute != null ? versionAttribute.toString() : "";
		}

		if (packageName.isEmpty()) {
			LOG.warn("Name is empty for {}", context.getRequest().getPath());
			return null;
		}
		if (packageVersion.isEmpty()) {
			LOG.warn("Version is empty for {}", context.getRequest().getPath());
			return null;
		}

		ScanResult scanResult = new ScanResult();

		if (checkmarxPropertiesExist(payload)) {
			LOG.debug("PYPI artifact {}:{} was already scanned. Skip scanning", packageName, packageVersion);

			NestedAttributesMap checkmarxSecurityMap = getCheckmarxSecurityAttributes(payload);
			Object risks = checkmarxSecurityMap.get("risks");
			LOG.debug("risks: {}", risks);
		} else {
			List<TestResult> testResult = null;
			try {
				Package pypiPackage = new Package(PackageType.PYPI.getType(), packageName, packageVersion);
				LOG.info("pypiPackage: {}", pypiPackage);
				ArrayList<Package> packages = new ArrayList<>();
				packages.add(pypiPackage);
				LOG.info("Scanning pypi packages: {}", packages);
				Call<List<TestResult>> call = CheckmarxClient.analyzePackage(packages, API_TOKEN.propertyKey());
				Response<List<TestResult>> response = call.execute();

				LOG.info("testPypi response: {}", response);

				if (response.isSuccessful() && response.body() != null) {
					testResult = response.body();
					String responseAsText = new ObjectMapper().writeValueAsString(response.body());
					LOG.debug("testPypi response: {}", responseAsText);
				}

				int risksCount = 0;
				List<String> risksList = new ArrayList<>();

				if (testResult != null) {
					updateAssetAttributes((TestResult) testResult, packageName, packageVersion, payload);
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
				LOG.error("Cloud not test pypi artifact: {}", context.getRequest().getPath(), ex);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

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

	private void updateAssetAttributes(@Nonnull TestResult testResult, @Nonnull String packageName, @Nonnull String packageVersion, Payload payload) {
		if (payload instanceof Content) {
			Asset asset = ((Content) payload).getAttributes().get(Asset.class);
			if (asset == null) {
				return;
			}

			NestedAttributesMap checkmarxSecurityMap = asset.attributes().child("checkmarx Security");
			checkmarxSecurityMap.clear();

			checkmarxSecurityMap.set("risks", testResult.risks);
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
