package com.checkmarx.sdk.api.v1;

import com.checkmarx.sdk.model.NotificationSettings;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.ArrayList;
import java.util.List;

public interface CheckmarxClient {

  /**
   * Get the user notification settings that will determine which emails are sent.
   */
  @GET("user/me/notification-settings")
  Call<NotificationSettings> getNotificationSettings();

  /**
   * Test Maven packages for issues according to their coordinates: group ID, artifact ID and version:
   * <ul>
   * <li><strong>groupId</strong></li>
   * <li><strong>artifactId</strong></li>
   * <li><strong>version</strong></li>
   * <li>organisation (optional)</li>
   * <li>repository (optional)</li>
   * </ul>
   */
  @POST("packages/")
  Call<List<PackageResponse>> analyzePackage(@Body ArrayList<PackageRequest> packageRequests);

  /**
   * Test NPM packages for issues according to their name and version:
   * <ul>
   * <li><strong>packageName</strong></li>
   * <li><strong>version</strong></li>
   * <li>organisation (optional)</li>
   * </ul>
   */

    void close();
}
