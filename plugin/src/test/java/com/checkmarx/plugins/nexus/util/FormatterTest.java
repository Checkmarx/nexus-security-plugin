package com.checkmarx.plugins.nexus.util;

import com.checkmarx.plugins.nexus.model.ScanResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FormatterTest {

  @Test
  void addScanRisks() {
    // given
    ScanResult scanResult = new ScanResult();

    // when
    scanResult.addRisks(Arrays.asList("data exfiltration", "spam", "obfuscated"));

    // then
    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(3, scanResult.risksCount);
      });
  }

  @Test
  void addScanToExistingRisks() {
    ScanResult scanResult = new ScanResult();
    scanResult.addRisks(Arrays.asList("data exfiltration", "spam", "obfuscated"));

    scanResult.addRisks(Arrays.asList("cryptominer", "malware"));
    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(5, scanResult.risksCount);
      });
  }

  @Test
  void addScanToExistingRisksWithEmptyList() {
    ScanResult scanResult = new ScanResult();
    scanResult.addRisks(Arrays.asList("data exfiltration", "spam", "obfuscated"));

    scanResult.addRisks(Collections.emptyList());
    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(3, scanResult.risksCount);
      });
  }

}
