package io.snyk.plugins.nexus.scanner;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.snyk.sdk.api.v1.SnykClient;
import io.snyk.sdk.model.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.nexus.common.collect.NestedAttributesMap;
import org.sonatype.nexus.repository.storage.Asset;
import org.sonatype.nexus.repository.storage.AssetStore;
import org.sonatype.nexus.repository.view.Content;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Payload;
import retrofit2.Response;

import static io.snyk.sdk.util.Formatter.getIssuesAsFormattedString;

@Named
public class NpmScanner {
  private static final Logger LOG = LoggerFactory.getLogger(NpmScanner.class);

  private final AssetStore assetStore;

  @Inject
  public NpmScanner(final AssetStore assetStore) {
    this.assetStore = assetStore;
  }

  TestResult scan(@Nonnull Context context, Payload payload, SnykClient snykClient, String organizationId) {
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

      if (!asset.name().endsWith("tgz")) {
        LOG.debug("Only 'tgz' extension is supported. Skip scanning");
        return null;
      }

      NestedAttributesMap npmAttributes = asset.attributes().child("npm");
      Object nameAttribute = npmAttributes.get("name");
      packageName = nameAttribute != null ? nameAttribute.toString() : "";
      Object versionAttribute = npmAttributes.get("version");
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

    TestResult testResult = null;
    try {
      Response<TestResult> response = snykClient.testNpm(packageName,
                                                         packageVersion,
                                                         organizationId).execute();
      if (response.isSuccessful() && response.body() != null) {
        testResult = response.body();
        String responseAsText = new ObjectMapper().writeValueAsString(response.body());
        LOG.debug("testNpm response: {}", responseAsText);
      }

      if (testResult != null) {
        updateAssetAttributes(testResult, packageName, packageVersion, payload);
      }

    } catch (IOException ex) {
      LOG.error("Cloud not test npm artifact: {}", context.getRequest().getPath(), ex);
    }

    return testResult;
  }

  private void updateAssetAttributes(@Nonnull TestResult testResult, @Nonnull String packageName, @Nonnull String packageVersion, Payload payload) {
    if (payload instanceof Content) {
      Asset asset = ((Content) payload).getAttributes().get(Asset.class);
      if (asset == null) {
        return;
      }

      NestedAttributesMap snykSecurityMap = asset.attributes().child("Snyk Security");
      snykSecurityMap.clear();

      snykSecurityMap.set("issues_vulnerabilities", getIssuesAsFormattedString(testResult.issues.vulnerabilities));
      snykSecurityMap.set("issues_licenses", getIssuesAsFormattedString(testResult.issues.licenses));
      StringBuilder snykIssueUrl = new StringBuilder("https://snyk.io/vuln/");
      snykIssueUrl.append(testResult.packageManager).append(":")
                  .append(packageName).append("@")
                  .append(packageVersion);
      snykSecurityMap.set("issues_url", snykIssueUrl.toString());

      assetStore.save(asset);
    }
  }
}
