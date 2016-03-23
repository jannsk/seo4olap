package com.breucker.seo4olap.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breucker.seo4olap.main.ConfigurationManager;
import com.breucker.seo4olap.main.ConfigurationManagerFactory;


@SuppressWarnings("serial")
public class RobotsServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		resp.setContentType("text/plain");
		
		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		
		resp.getWriter().println("# www.robotstxt.org/ \n");
		resp.getWriter().println("User-agent: *");
		resp.getWriter().println("Disallow:");
		resp.getWriter().println("Sitemap: " + configManager.getBaseUri() + "/sitemap.xml");
	}
}
