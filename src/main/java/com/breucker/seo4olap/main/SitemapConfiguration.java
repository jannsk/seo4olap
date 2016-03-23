package com.breucker.seo4olap.main;

public class SitemapConfiguration {

	private String endpoint = "default";
	private int maxDimensionCount = 2;
	private int minDimensionCount = 0;
	private int maxMemberCount = 2;
	private int minMemberCount = 0;
	private int maxMeasureCount = 2;
	private int minMeasureCount = 1;
	
	public SitemapConfiguration() {}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public int getMaxDimensionCount() {
		return maxDimensionCount;
	}

	public void setMaxDimensionCount(int maxDimensionCount) {
		this.maxDimensionCount = maxDimensionCount;
	}

	public int getMinDimensionCount() {
		return minDimensionCount;
	}

	public void setMinDimensionCount(int minDimensionCount) {
		this.minDimensionCount = minDimensionCount;
	}

	public int getMaxMemberCount() {
		return maxMemberCount;
	}

	public void setMaxMemberCount(int maxMemberCount) {
		this.maxMemberCount = maxMemberCount;
	}

	public int getMinMemberCount() {
		return minMemberCount;
	}

	public void setMinMemberCount(int minMemberCount) {
		this.minMemberCount = minMemberCount;
	}

	public int getMaxMeasureCount() {
		return maxMeasureCount;
	}

	public void setMaxMeasureCount(int maxMeasureCount) {
		this.maxMeasureCount = maxMeasureCount;
	}

	public int getMinMeasureCount() {
		return minMeasureCount;
	}

	public void setMinMeasureCount(int minMeasureCount) {
		this.minMeasureCount = minMeasureCount;
	}
	
	public SitemapConfiguration clone(){
		SitemapConfiguration clone = new SitemapConfiguration();
		clone.setEndpoint(endpoint);
		clone.setMaxDimensionCount(maxDimensionCount);
		clone.setMaxMeasureCount(maxMeasureCount);
		clone.setMaxMemberCount(maxMemberCount);
		clone.setMinDimensionCount(minDimensionCount);
		clone.setMinMeasureCount(minMeasureCount);
		clone.setMinMemberCount(minMemberCount);
		return clone;
	}
	
}
