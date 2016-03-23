package com.breucker.seo4olap.main;

import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a sitemap for the given Dataset
 * @author Daniel Breucker
 * 
 */
public class SitemapGenerator {

	private final List<Link> absoluteLinks; 
	private final List<Link> relativeLinks; 
	
	/**
	 * Returns a new SitemapGenerator
	 * @param datasetUri The datasetUrl from which a sitemap should be created
	 */
	public SitemapGenerator(URL datasetUri) throws InvalidParameterException{
		if(datasetUri == null){
			throw new InvalidParameterException("datasetUri cannot be null");
		}
		RequestHandler handler = new RequestHandler();
		List<String> absoluteRequests = handler.getUrlRequestList(datasetUri, false, true);
		List<Link> absoluteLinks = new ArrayList<Link>();
		//add Overview link
		Link overviewLink = LinkGenerator.getDatasetLink(datasetUri, true);
		if(overviewLink != null){
			absoluteLinks.add(overviewLink);
		}
		for(String request: absoluteRequests){
			Link link = LinkGenerator.getLink(request);
			if(link != null){
				absoluteLinks.add(link);
			}
		}
		this.absoluteLinks = absoluteLinks;
		

		List<String> relativeRequests = handler.getUrlRequestList(datasetUri, false, false);
		List<Link> relativeLinks = new ArrayList<Link>();
		//add Overview link
		Link overviewLink2 = LinkGenerator.getDatasetLink(datasetUri, false);
		if(overviewLink2 != null){
			relativeLinks.add(overviewLink2);
		}
		for(String request: relativeRequests){
			Link link = LinkGenerator.getLink(request);
			if(link != null){
				relativeLinks.add(link);
			}
		}
		this.relativeLinks = relativeLinks;
	}
	
	/**
	 * Get the overall sitemap in XML, which points to specific Sitemaps. Should be used at /sitemap.xml for SearchEngine Crawlers
	 * @return
	 */
	public static String getGeneralSitemap(){
		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		List<URL> datasetUris =  configManager.getDatasetUris();
		String baseUri = configManager.getBaseUri();
		if(baseUri == null){
			return null;
		}
		String sitemap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>  "
				+ "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
		
		sitemap += "<sitemap><loc>" + baseUri + "/sitemapep?dsid=staticsites&amp;format=xml</loc> </sitemap>";
		
		for(URL datasetUri: datasetUris){
			String datasetId = configManager.getDatasetId(datasetUri);
			if(datasetId != null){
				sitemap += "<sitemap><loc>" + baseUri + "/sitemapep?dsid=" + datasetId + "&amp;format=xml</loc> </sitemap>";
			}
		}		
		sitemap += "</sitemapindex>";
		return sitemap;
	}
	
	/**
	 * Returns a Sitemap of all static Sites (defined in Configuration)
	 * @param format
	 * @return String of Sitemap in requested Format, empty String of no static files were set in Configuration
	 */
	public static String getStaticSitesSitemap(SitemapFormat format){
		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		List<Link> links =  configManager.getStaticSites();
		if(links == null){
			return "";
		}
		return produceSitemap(links, format);
	}	
	
	/**
	 * Get the Sitemap in specified format.
	 * @param format
	 * @return
	 */
	public String getSitemap(SitemapFormat format){
		if(format == SitemapFormat.HTML){
			return produceSitemap(this.relativeLinks, format);
		}
		return produceSitemap(this.absoluteLinks, format);
	}
	
	
	/**
	 * Get all Links from the Sitemap as a List of Links
	 * @return
	 */
	public List<Link> getSitemapLinks(boolean absolutePath){
		if(absolutePath){
			return this.absoluteLinks;
		}
		return this.relativeLinks;
	}

	/*#############------------####################
	 * 
	 * Private Methods
	 * 
	 *#############------------####################*/

	private static String produceSitemap(List<Link> links, SitemapFormat format){
		String sitemap = "";
		switch(format){
		case XML:
			sitemap += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n";
			sitemap	+= "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" + "\n";
			for(Link link : links){
				sitemap += "\t" + "<url>" + "\n";
				sitemap += "\t\t" + "<loc>" + link.getUrl() + "</loc>" + "\n";
				sitemap += "\t" + "</url>"+ "\n";
			}
			sitemap += "</urlset>" + "\n";
			return sitemap;
		case HTML:
			sitemap += "<div id=\"sitemap\">";
			for(Link link : links){
				sitemap += "<a href=\"" + link.getUrl() + "\">" + link.getText() + "</a><br>" ;
			}
			sitemap += "</div>";
			return sitemap;
		default:
			for(Link link : links){
				sitemap += link.getUrl() + "\n";
			}
			return sitemap;
		}
	}
	
	public enum SitemapFormat {
	    PLAIN, XML, HTML
	}
}

