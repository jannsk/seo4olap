package com.breucker.seo4olap.main;


public class MeasureMemberConfiguration extends MetadataConfiguration {

	private String measureMemberUniqueName = null;
	
	public MeasureMemberConfiguration() {}

	public MeasureMemberConfiguration(String uniqueName, String id, String label) {
		super(uniqueName, id, label);
	}

	public String getMeasureMemberUniqueName() {
		return measureMemberUniqueName;
	}

	public void setMeasureMemberUniqueName(String measureMemberUniqueName) {
		this.measureMemberUniqueName = measureMemberUniqueName;
	}

}
