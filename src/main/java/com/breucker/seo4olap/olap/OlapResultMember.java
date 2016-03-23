package com.breucker.seo4olap.olap;

public class OlapResultMember {

	private String uniqueName = null;
	private String label = null;
	private String dimensionUniqueName = null;
	
	public OlapResultMember() {}

	public OlapResultMember(String uniqueName, String label, String dimensionUniqueName) {
		this.uniqueName = uniqueName;
		this.label = label;
		this.dimensionUniqueName = dimensionUniqueName;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDimensionUniqueName() {
		return dimensionUniqueName;
	}

	public void setDimensionUniqueName(String dimensionUniqueName) {
		this.dimensionUniqueName = dimensionUniqueName;
	}

}
