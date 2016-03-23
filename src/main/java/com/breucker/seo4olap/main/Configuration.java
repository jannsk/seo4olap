package com.breucker.seo4olap.main;

import java.util.List;

public class Configuration {

	private List<DatasetConfiguration> datasets = null;
	private boolean isDebugMode = false;
	private String baseUri = null;
	private List<Link> staticSites = null;
	private String version = "default";

	public Configuration() {}
	
	public List<DatasetConfiguration> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<DatasetConfiguration> datasets) {
		this.datasets = datasets;
	}

	public boolean isDebugMode() {
		return isDebugMode;
	}

	public void setDebugMode(boolean isDebugMode) {
		this.isDebugMode = isDebugMode;
	}

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public List<Link> getStaticSites() {
		return staticSites;
	}

	public void setStaticSites(List<Link> staticSites) {
		this.staticSites = staticSites;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
