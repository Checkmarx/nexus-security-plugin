package com.checkmarx.plugins.nexus.util;

import com.checkmarx.plugins.nexus.model.ScanResult;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;

public final class Formatter {

  private Formatter() {
  }

  public static long getRisksCount(@Nonnull List<?> risks) {
    return risks.size();
  }

  public static String getRisksAsFormattedString(@Nonnull ScanResult scanResult) {
    StringBuilder sb = new StringBuilder();
    sb.append("Checkmarx scan results:\n");
    sb.append("Total risks: ").append(scanResult.risksCount).append("\n");
    String risksTypeAsString = StringUtils.join(scanResult.risksType, "");
    sb.append("type: ").append(risksTypeAsString).append("\n");
    return sb.toString();

  }
}
