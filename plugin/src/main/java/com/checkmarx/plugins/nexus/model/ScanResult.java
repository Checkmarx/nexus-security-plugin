package com.checkmarx.plugins.nexus.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class ScanResult {
  public long risksCount = 0;
  public List<String> risksType = new ArrayList<>(Collections.singletonList(""));



  public ScanResult() {
  }

  public void addRisks(List<String> newRisks) {

    if (!Objects.equals(risksType.get(0), "")) {
      newRisks = Stream.concat(risksType.stream(), newRisks.stream())
        .collect(Collectors.toList());
    }
    this.risksType = newRisks;
    this.risksCount = risksType.size();
  }


  public ScanResult(long risksCount, List<String> risksType) {
    this.risksCount = risksCount;
    this.risksType = risksType;
  }
}
