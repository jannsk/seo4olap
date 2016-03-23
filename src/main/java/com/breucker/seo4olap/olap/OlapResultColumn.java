package com.breucker.seo4olap.olap;

import java.util.Set;

public class OlapResultColumn {

	private OlapResultColumnType type = null;
	private String uniqueName = null;
	private String label = null;
	private String description = null;
	private String measureAggregator = null;
	private String isVisible = null;
	private Set<String> members = null;
	
	public OlapResultColumn() {}

	
	public OlapResultColumnType getType() {
		return type;
	}

	public void setType(OlapResultColumnType type) {
		this.type = type;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMeasureAggregator() {
		return measureAggregator;
	}

	public void setMeasureAggregator(String measureAggregator) {
		this.measureAggregator = measureAggregator;
	}

	public String getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(String isVisible) {
		this.isVisible = isVisible;
	}

	public Set<String> getMembers() {
		return members;
	}
	
	public void setMembers(Set<String> members) {
		this.members = members;
	}

}
