package com.checkmarx.sdk.api.v1;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The test result is the object returned from the API giving the results of testing a package
 * for issues.
 */
public class PackageResponse implements Serializable {
	Logger Log = Logger.getLogger(PackageResponse.class.getName());

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
