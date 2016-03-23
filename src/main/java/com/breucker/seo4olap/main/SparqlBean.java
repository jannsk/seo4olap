package com.breucker.seo4olap.main;

public class SparqlBean{

	private String query;
	private String result;
	private String dsId;

	public SparqlBean() {}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public void setDsId(String dsId) {
		this.dsId = dsId;
	}
	
	public String getQuery() {
		return query;
	}

	public String getDsId() {
		return dsId;
	}

	public String getResult() {
		return result;
	}
	
}
