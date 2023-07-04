package com.checkmarx.sdk.model;

import com.checkmarx.sdk.util.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import sun.rmi.runtime.Log;

/**
 * The test result is the object returned from the API giving the results of testing a package
 * for issues.
 */
public class TestResult implements Serializable {
	Logger Log = Logger.getLogger(TestResult.class.getName());

	private static final long serialVersionUID = 1L;

	@JsonProperty("name")
	public String name;
	@JsonProperty("type")
	public String type;
	@JsonProperty("version")
	public String version;
	@JsonProperty("status")
	public String status;
	@JsonProperty("ioc")
	public List<String> ioc;
	@JsonProperty("risks")
	public List<Map<String, String>> risks = null;
}
