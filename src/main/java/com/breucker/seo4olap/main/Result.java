package com.breucker.seo4olap.main;

import java.util.List;

public class Result{

	private String metadataTitle = "dummyTitle";
	private String metadataDescription = "dummy Descrption";
	private String metadataKeywords = "dummy keywords";
	private Link sourceLink = null;
	private Link licenceLink = null;
	private Link overviewLink = null;
	private String datasetDescription = null;
	private String queryTitle = null;
	private String[] filters = null;
	private String[][] table = null;	
	private List<Link> changeViewLinks = null;
	private List<Link> filterLinks = null;
	private ResultDebugInformation debugInformation = null;
	
	public Result() {}

	public String getMetadataTitle() {
		return metadataTitle;
	}

	public void setMetadataTitle(String metadataTitle) {
		this.metadataTitle = metadataTitle;
	}

	public String getMetadataDescription() {
		return metadataDescription;
	}

	public void setMetadataDescription(String metadataDescription) {
		this.metadataDescription = metadataDescription;
	}

	public String getMetadataKeywords() {
		return metadataKeywords;
	}

	public void setMetadataKeywords(String metadataKeywords) {
		this.metadataKeywords = metadataKeywords;
	}

	public Link getSourceLink() {
		return sourceLink;
	}

	public void setSourceLink(Link sourceLink) {
		this.sourceLink = sourceLink;
	}

	public Link getLicenceLink() {
		return licenceLink;
	}

	public void setLicenceLink(Link licenceLink) {
		this.licenceLink = licenceLink;
	}

	public String getDatasetDescription() {
		return datasetDescription;
	}

	public void setDatasetDescription(String datasetDescription) {
		this.datasetDescription = datasetDescription;
	}

	public String getQueryTitle() {
		return queryTitle;
	}

	public void setQueryTitle(String queryTitle) {
		this.queryTitle = queryTitle;
	}

	public String[] getFilters() {
		return filters;
	}

	public void setFilters(String[] filters) {
		this.filters = filters;
	}

	public String[][] getTable() {
		return table;
	}

	public void setTable(String[][] table) {
		this.table = table;
	}

	public List<Link> getChangeViewLinks() {
		return changeViewLinks;
	}

	public void setChangeViewLinks(List<Link> links) {
		this.changeViewLinks = links;
	}

	public List<Link> getFilterLinks() {
		return filterLinks;
	}

	public void setFilterLinks(List<Link> filterLinks) {
		this.filterLinks = filterLinks;
	}

	public ResultDebugInformation getDebugInformation() {
		return debugInformation;
	}

	public void setDebugInformation(ResultDebugInformation debugInformation) {
		this.debugInformation = debugInformation;
	}

	public Link getOverviewLink() {
		return overviewLink;
	}

	public void setOverviewLink(Link overviewLink) {
		this.overviewLink = overviewLink;
	}
	
}
