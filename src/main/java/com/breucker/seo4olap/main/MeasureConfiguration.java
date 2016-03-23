package com.breucker.seo4olap.main;

import java.util.List;

public class MeasureConfiguration extends MetadataConfiguration {

	private List<MeasureMemberConfiguration> measureMembers = null;
	
	public MeasureConfiguration() {}

	public MeasureConfiguration(String uniqueName, String id, String label) {
		super(uniqueName, id, label);
	}

	public List<MeasureMemberConfiguration> getMeasureMembers() {
		return measureMembers;
	}

	public void setMeasureMembers(List<MeasureMemberConfiguration> measureMembers) {
		this.measureMembers = measureMembers;
	}

}
