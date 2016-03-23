package com.breucker.seo4olap.server;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.olap4j.OlapException;

import com.breucker.seo4olap.main.ConfigurationManager;
import com.breucker.seo4olap.main.ConfigurationManagerFactory;
import com.breucker.seo4olap.main.PresentationHelper;
import com.breucker.seo4olap.main.RequestHandler;


@SuppressWarnings("serial")
public class MetadataServlet extends HttpServlet {
	public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		resp.setContentType("text/html");
		
		String dsId = req.getParameter(RequestParameter.DATASET_ID);
		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		URL dsUri = configManager.getDatasetUriOfId(dsId);
		String metadata;
		if(dsUri == null){
			metadata = PresentationHelper.generateErrorMessage();
		}
		else{
			try {
				metadata = new RequestHandler().getMetadata(dsUri);
			} catch (OlapException e) {
				metadata = PresentationHelper.generateErrorMessage();
				e.printStackTrace();
			}
		}
		
		req.setAttribute("metadata", metadata);
		//forward to jsp
		req.getRequestDispatcher("/jsps/metadata.jsp").forward(req, resp);
		
	}
}
