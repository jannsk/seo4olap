package com.breucker.seo4olap.main;

import java.util.List;

public class IndexBean {

	private String title;
	private String description;
	private List<DatasetBean> datasets;
	
	public IndexBean() {}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<DatasetBean> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<DatasetBean> datasets) {
		this.datasets = datasets;
	}

}
