package com.breucker.seo4olap.server;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breucker.seo4olap.main.ConfigurationManager;
import com.breucker.seo4olap.main.ConfigurationManagerFactory;
import com.breucker.seo4olap.main.SitemapBean;
import com.breucker.seo4olap.main.SitemapGenerator;
import com.breucker.seo4olap.main.SitemapGenerator.SitemapFormat;

@SuppressWarnings("serial")
public class SitemapServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(SitemapServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = req.getParameterMap();
		logger.info("Incoming Request. RequestParameter: " + ServerUtils.parameterMapToString(parameterMap));		
		
		String formatParam = req.getParameter(RequestParameter.FORMAT);
		if(formatParam != null && formatParam.equals("html")){
			getHtmlSitemap(req, resp);
			return;			
		}
		
		String dsId = req.getParameter(RequestParameter.DATASET_ID);
		if(dsId != null && dsId.equals("all")){
			resp.setContentType("text/xml");
			resp.getWriter().print(SitemapGenerator.getGeneralSitemap());
			return;
		}
		else if(dsId != null && dsId.equals("staticsites")){
			resp.setContentType("text/xml");
			resp.getWriter().print(SitemapGenerator.getStaticSitesSitemap(SitemapFormat.XML));
			return;
		}
		else {
			getSitemap(req, resp);
		}
	}
	
	private void getHtmlSitemap(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		
		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		List<SitemapBean> sitemaps = new ArrayList<SitemapBean>();
		List<URL> datasetUris = configManager.getDatasetUris();
		for(URL datasetUri : datasetUris){
			SitemapBean sitemap = new SitemapBean();
			try{
				SitemapGenerator generator = new SitemapGenerator(datasetUri);
				sitemap.setLinks(generator.getSitemapLinks(false));
				String title = configManager.getDatasetTitle(datasetUri);
				if(title !=null){
					sitemap.setTitle(configManager.getDatasetTitle(datasetUri));
				}
				sitemaps.add(sitemap);
				
			} catch(InvalidParameterException e){
				continue;
			}
		}
		req.setAttribute("sitemaps", sitemaps);
		req.getRequestDispatcher("/jsps/sitemap.jsp").forward(req, resp);
	}
	
	private void getSitemap(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		
		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		
		String dsId = req.getParameter(RequestParameter.DATASET_ID);
		URL dsUri = configManager.getDatasetUriOfId(dsId);
		
		if(dsUri == null){
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			req.getRequestDispatcher("/errors/404.html").forward(req, resp);
			return;
		}
		String formatParam = req.getParameter(RequestParameter.FORMAT);
		SitemapFormat format = SitemapFormat.PLAIN;
		resp.setContentType("text/plain");
		if(formatParam != null && formatParam.equals("xml")){
			format = SitemapFormat.XML;
			resp.setContentType("text/xml");
		}		
		
		SitemapGenerator generator = new SitemapGenerator(dsUri);
		String sitemap = generator.getSitemap(format);
		resp.getWriter().print(sitemap);
	}
}
