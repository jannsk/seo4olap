package com.breucker.seo4olap.main;

public class MetadataConfiguration {

	private String uniqueName = null;
	private String id = null;
	private String label = null;
	

	public MetadataConfiguration() {}

	public MetadataConfiguration(String uniqueName, String id, String label) {
		this.uniqueName = uniqueName;
		this.id = id;
		this.label = label;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}
