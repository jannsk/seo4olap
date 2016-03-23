package com.breucker.seo4olap.main;

import java.util.List;

public class DatasetConfiguration {

	private String id = null;
	private String uri = null;
	private boolean initOnStartup = true;
	private String description = null;
	private String title = null;
	private Link licence = null;
	private Link source = null;
	private SitemapConfiguration sitemap = null;
	private List<DimensionConfiguration> dimensions = null;
	private List<MeasureConfiguration> measures = null;
	private List<DimensionConfiguration> dimensionMeasures = null;
	
	public DatasetConfiguration() {}

	public String getId() {
		return id;
	}

	public void setId(String dsId) {
		this.id = dsId;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String dsUri) {
		this.uri = dsUri;
	}

	public boolean isInitOnStartup() {
		return initOnStartup;
	}

	public void setLoadOnStartup(boolean initOnStartup) {
		this.initOnStartup = initOnStartup;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public SitemapConfiguration getSitemap() {
		return sitemap;
	}

	public void setSitemap(SitemapConfiguration sitemap) {
		this.sitemap = sitemap;
	}

	public List<DimensionConfiguration> getDimensions() {
		return dimensions;
	}

	public void setDimensions(List<DimensionConfiguration> dimensions) {
		this.dimensions = dimensions;
	}

	public List<MeasureConfiguration> getMeasures() {
		return measures;
	}

	public void setMeasures(List<MeasureConfiguration> measures) {
		this.measures = measures;
	}

	public List<DimensionConfiguration> getDimensionMeasures() {
		return dimensionMeasures;
	}

	public void setDimensionMeasures(List<DimensionConfiguration> dimensionMeasures) {
		this.dimensionMeasures = dimensionMeasures;
	}
	
}
