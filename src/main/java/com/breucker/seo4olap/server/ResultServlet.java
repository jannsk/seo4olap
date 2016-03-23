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
import com.breucker.seo4olap.main.OlapRequestGenerator;
import com.breucker.seo4olap.main.RequestHandler;
import com.breucker.seo4olap.main.Result;
import com.breucker.seo4olap.olap.OlapRequest;


@SuppressWarnings("serial")
public class ResultServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(ResultServlet.class.getName());
	
	public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = req.getParameterMap();
		
		logger.log(Level.INFO, "Incoming Request. RequestParameter: " + ServerUtils.parameterMapToString(parameterMap));
		
		resp.setContentType("text/html");
		
		String recomputeString = req.getParameter(RequestParameter.RECOMPUTE);
		boolean recompute = false;
		if(recomputeString != null && recomputeString.equals("true")){
			recompute = true;
		}
		
		//make Request
		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		
		String dsEndpoint = req.getParameter(RequestParameter.DATASET_ENDPOINT);
		String dsId = req.getParameter(RequestParameter.DATASET_ID);
		URL dsUri = null;
		if(dsEndpoint != null){
			dsUri = configManager.getDatasetUriOfEndpoint(dsEndpoint);
		}
		else if(dsId != null){
			dsUri = configManager.getDatasetUriOfId(dsId);
		}
		if(dsUri == null){
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			req.getRequestDispatcher("/errors/404.html").forward(req, resp);
			return;
		}
				
		try {
			OlapRequestGenerator generator = new OlapRequestGenerator(dsUri);
			OlapRequest olapRequest = generator.generateOlapRequest(parameterMap);
			RequestHandler requestHandler = new RequestHandler();
			Result result = requestHandler.getOlapResult(olapRequest, recompute);
			
			req.setAttribute("runtimeContext", requestHandler.getRuntimeContext(false));
			req.setAttribute("result", result);
			req.getRequestDispatcher("/jsps/result.jsp").forward(req, resp);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error for RequestParameter: " + ServerUtils.parameterMapToString(parameterMap), e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			req.getRequestDispatcher("/errors/default-error.html").forward(req, resp);
			return;
		}
	}
}
