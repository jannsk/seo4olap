package com.breucker.seo4olap.main;

import java.util.List;

public class DimensionConfiguration extends MetadataConfiguration {

	private String sliceMemberUniqueName = null;
	private String sliceLabel = null;
	private List<MetadataConfiguration> members = null;
	
	public DimensionConfiguration() {}

	public DimensionConfiguration(String uniqueName, String id, String label) {
		super(uniqueName, id, label);
	}

	public String getSliceMemberUniqueName() {
		return sliceMemberUniqueName;
	}

	public void setSliceMemberUniqueName(String sliceMemberUniqueName) {
		this.sliceMemberUniqueName = sliceMemberUniqueName;
	}

	public List<MetadataConfiguration> getMembers() {
		return members;
	}

	public void setMembers(List<MetadataConfiguration> members) {
		this.members = members;
	}

	public String getSliceLabel() {
		return sliceLabel;
	}

	public void setSliceLabel(String sliceLabel) {
		this.sliceLabel = sliceLabel;
	}

}
