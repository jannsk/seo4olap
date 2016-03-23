package com.breucker.seo4olap.server;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.breucker.seo4olap.main.ConfigurationManager;
import com.breucker.seo4olap.main.ConfigurationManagerFactory;
import com.breucker.seo4olap.main.DatasetBean;
import com.breucker.seo4olap.main.RequestHandler;


@SuppressWarnings("serial")
public class DatasetServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(DatasetServlet.class.getName());
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = req.getParameterMap();	
		logger.log(Level.INFO, "Incoming Request. RequestParameter: " + ServerUtils.parameterMapToString(parameterMap));
		
		resp.setContentType("text/html");

		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		
		String dsEndpoint = req.getParameter(RequestParameter.DATASET_ENDPOINT);
		URL dsUri = null;
		if(dsEndpoint != null){
			dsUri = configManager.getDatasetUriOfEndpoint(dsEndpoint);
		}
		if(dsUri == null){
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			req.getRequestDispatcher("/errors/404.html").forward(req, resp);
			return;
		}
		
		RequestHandler requestHandler = new RequestHandler();
		DatasetBean dsBean = requestHandler.getDatasetBean(dsUri, false);
		
		req.setAttribute("runtimeContext", requestHandler.getRuntimeContext(false));
		req.setAttribute("dataset", dsBean);
		req.getRequestDispatcher("/jsps/dataset.jsp").forward(req, resp);
	}
}
