package com.breucker.seo4olap.main;

public class Link {

	private String url = null;
	private String text = null;
	
	public Link() {}
	
	public Link(String url, String text) {
		this.url = url;
		this.text = text;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
}
