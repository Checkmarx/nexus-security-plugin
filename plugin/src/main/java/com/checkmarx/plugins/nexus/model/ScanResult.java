package com.checkmarx.plugins.nexus.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class ScanResult {
	private long risksCount = 0;
	private final Set<String> risks;

	public ScanResult() {
		risks = new HashSet<>();
	}

	public void addRisk(String risk) {
		risks.add(risk);
		risksCount += 1;
	}

	public long getRisksCount() {
		return risksCount;
	}

	public void setRisksCount(long risksCount) {
		this.risksCount = risksCount;
	}

	public Set<String> getRisks() {
		return risks;
	}

}
