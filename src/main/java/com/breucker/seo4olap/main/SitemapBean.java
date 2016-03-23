package com.breucker.seo4olap.main;

import java.util.List;

public class SitemapBean {

	private List<Link> links = null;
	private String title = null;
	
	public SitemapBean() {}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
