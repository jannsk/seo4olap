package com.breucker.seo4olap.olap;

public class OlapResultInformation {

	private String datasetComment = null;
	private String datasetDescription = null;
	private String datasetLicence = null;
	private String[] keywords = null;
	private int measureCount = 0;
	private int dimensionCount = 0;
	
	public OlapResultInformation() {}

	public OlapResultInformation(String datasetComment, String datasetDescription, String datasetLicence,
			String[] keywords, int measureCount, int dimensionCount, int columnCount) {
		this.datasetComment = datasetComment;
		this.datasetDescription = datasetDescription;
		this.datasetLicence = datasetLicence;
		this.keywords = keywords;
		this.measureCount = measureCount;
		this.dimensionCount = dimensionCount;
	}

	public String getDatasetComment() {
		return datasetComment;
	}

	public void setDatasetComment(String datasetComment) {
		this.datasetComment = datasetComment;
	}

	public String getDatasetDescription() {
		return datasetDescription;
	}

	public void setDatasetDescription(String datasetDescription) {
		this.datasetDescription = datasetDescription;
	}

	public String getDatasetLicence() {
		return datasetLicence;
	}

	public void setDatasetLicence(String datasetLicence) {
		this.datasetLicence = datasetLicence;
	}

	public String[] getKeywords() {
		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

	public int getMeasureCount() {
		return measureCount;
	}

	public void setMeasureCount(int measureCount) {
		this.measureCount = measureCount;
	}

	public int getDimensionCount() {
		return dimensionCount;
	}

	public void setDimensionCount(int dimensionCount) {
		this.dimensionCount = dimensionCount;
	}
	
}
