package com.breucker.seo4olap.server;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breucker.seo4olap.main.ConfigurationManager;
import com.breucker.seo4olap.main.ConfigurationManagerFactory;
import com.breucker.seo4olap.main.RequestHandler;
import com.breucker.seo4olap.main.SparqlBean;


@SuppressWarnings("serial")
public class SparqlServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		resp.setContentType("text/html");

		req.getRequestDispatcher("/jsps/admin.jsp").forward(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		resp.setContentType("text/html");
		   
		String query = req.getParameter(RequestParameter.QUERY);
		String dsId = req.getParameter(RequestParameter.DATASET_ID);
		
		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		URL dsUri = configManager.getDatasetUriOfId(dsId);
		
		String result = "";
		if(dsUri == null){
			result = "Dataset not found";
		}
		else{
			result = new RequestHandler().getSparqlResult(dsUri, query);
		}
		
		SparqlBean bean =  new SparqlBean();
		bean.setDsId(dsId);
		bean.setQuery(query);
		bean.setResult(result);
		req.setAttribute("sparqlBean", bean);
		
		//forward to jsp
		req.getRequestDispatcher("/jsps/admin.jsp").forward(req, resp);
	}
}
