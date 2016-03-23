package com.breucker.seo4olap.main;

import java.util.List;

import com.breucker.seo4olap.olap.OlapRequest;

public class ResultDebugInformation {

	private List<String> members2dice;
	private List<String> measures2project;
	private List<String> dimensions2keep;
	private String datasetUri;
	private List<String> dicedMembers;
	private List<String> projectedMeasures;
	private List<String> freeDimensions;

	public ResultDebugInformation() {}
	
	public ResultDebugInformation(OlapRequest olapRequest, List<String> dicedMembers, List<String> projectedMeasures,
			List<String> freeDimensions) {
		this.members2dice = olapRequest.getMembers2dice();
		this.measures2project = olapRequest.getMeasures2project();
		this.dimensions2keep = olapRequest.getDimensions2keep();
		this.datasetUri = olapRequest.getDatasetUri().toString();
		this.dicedMembers = dicedMembers;
		this.projectedMeasures = projectedMeasures;
		this.freeDimensions = freeDimensions;
	}
	
	
	public List<String> getDicedMembers() {
		return dicedMembers;
	}
	
	public List<String> getProjectedMeasures() {
		return projectedMeasures;
	}
	
	public List<String> getFreeDimensions() {
		return freeDimensions;
	}

	public List<String> getMembers2dice() {
		return members2dice;
	}

	public List<String> getMeasures2project() {
		return measures2project;
	}

	public List<String> getDimensions2keep() {
		return dimensions2keep;
	}

	public String getDatasetUri() {
		return datasetUri;
	}
	
}
