package com.breucker.seo4olap.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.olap4j.OlapException;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.Rio;

import com.breucker.seo4olap.main.ConfigurationManager;
import com.breucker.seo4olap.main.ConfigurationManagerFactory;
import com.breucker.seo4olap.main.RequestHandler;

@SuppressWarnings("serial")
public class RdfServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(RdfServlet.class.getName());
	
	public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		resp.setContentType("application/x-turtle");
		
		String dsId = req.getParameter(RequestParameter.DATASET_ID);
		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		URL dsUri = configManager.getDatasetUriOfId(dsId);
		
		String format = req.getParameter(RequestParameter.FORMAT);
		RDFFormat rdfFormat = RDFFormat.TURTLE;
		if(format != null){
			if(format.equals("n3")){
				rdfFormat = RDFFormat.N3;
			}
			if(format.equals("xml")){
				rdfFormat = RDFFormat.RDFXML;
			}
			if(format.equals("ntriples")){
				rdfFormat = RDFFormat.NTRIPLES;
			}
		}
		
		if(dsUri == null){
			req.getRequestDispatcher("404.html").forward(req, resp);
			return;
		}
		
		try {
			Model model = new RequestHandler().getRdfDataset(dsUri);
			Rio.write(model, resp.getWriter(), rdfFormat);
		} catch (RDFHandlerException e) {
			logger.warning("RDFHandlerException: " + e.getMessage());
			req.getRequestDispatcher("default_error.html").forward(req, resp);
		} catch (OlapException e) {
			logger.warning("OlapException: " + e.getMessage());
			req.getRequestDispatcher("default_error.html").forward(req, resp);
		} catch (MalformedURLException e) {
			logger.warning("URL = '" + dsUri +"' MalformedURLException: " + e.getMessage());
			req.getRequestDispatcher("default_error.html").forward(req, resp);
		}
		
	
	}
}
