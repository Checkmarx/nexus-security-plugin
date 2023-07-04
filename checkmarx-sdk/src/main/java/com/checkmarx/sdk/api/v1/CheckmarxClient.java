package com.checkmarx.sdk.api.v1;

import com.checkmarx.sdk.model.NotificationSettings;
import com.checkmarx.sdk.model.TestResult;
import retrofit2.Call;
import retrofit2.http.*;
import com.checkmarx.sdk.util.Package;
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
  Call<List<TestResult>> analyzePackage(@Body ArrayList<Package> packages, @Header("Authorization") String token);

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
