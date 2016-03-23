package com.breucker.seo4olap.main;

import java.util.List;

public class DatasetBean {

	private String title = null;
	private String description = null;
	private Link link = null;
	private Link licence = null;
	private Link source = null;
	private List<Link> promotedLinks = null;
	private List<Link> links = null;
	
	public DatasetBean() {}

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

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public Link getLicence() {
		return licence;
	}

	public void setLicence(Link licence) {
		this.licence = licence;
	}

	public Link getSource() {
		return source;
	}

	public void setSource(Link source) {
		this.source = source;
	}

	public List<Link> getPromotedLinks() {
		return promotedLinks;
	}

	public void setPromotedLinks(List<Link> promotedLinks) {
		this.promotedLinks = promotedLinks;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
}
