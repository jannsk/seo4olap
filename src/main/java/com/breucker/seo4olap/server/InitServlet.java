package com.breucker.seo4olap.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.olap4j.OlapException;

import com.breucker.seo4olap.main.ConfigurationManager;
import com.breucker.seo4olap.main.ConfigurationManagerFactory;
import com.breucker.seo4olap.main.DatabaseManager;
import com.breucker.seo4olap.main.RequestHandler;
import com.breucker.seo4olap.olap.OlapRequest;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@SuppressWarnings("serial")
public class InitServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(InitServlet.class.getName());
	private final ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
	
	public void init() throws ServletException {
		  logger.info("Init application.");
		  ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		  List<URL> dsURLs = configManager.getDatasetUris();

		  Queue queue = QueueFactory.getQueue("init-queue");
		  for(URL url: dsURLs){
			  if(configManager.isInitOnStartup(url)){
				  logger.info("Add to queue. url: " + url);
				  queue.add(TaskOptions.Builder.withUrl("/admin/init").param(RequestParameter.DATASET_URL, url.toString())
						  .param(RequestParameter.TASK, "initDataset"));
			  }
		  }
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp){
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = req.getParameterMap();
		
		logger.log(Level.INFO, "Incoming Request. RequestParameter: " + ServerUtils.parameterMapToString(parameterMap));
		
		String task = req.getParameter(RequestParameter.TASK);
		if(task == null){
			return;
		}
		if(task.equals("initDataset")){
			initDataset(req, resp);
		}
		if(task.equals("initLabelMap")){
			initLabelMap(req, resp);
		}
		if(task.equals("initDatabase")){
			initDatabase(req, resp);
		}
    }
	
	private void initDataset(HttpServletRequest request, HttpServletResponse response){
		  String paramUrl = request.getParameter(RequestParameter.DATASET_URL);
	        logger.info("Init application. Load datasetUri: " + paramUrl + " into tripleStore");
	        URL datasetUri = null;
			try {
				datasetUri = new URL(paramUrl);
			} catch (MalformedURLException e) {
		        logger.warning("Failed to load url: " + paramUrl + " due to MalformedURLException");
		        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		        return;
			}
	        RequestHandler requestHandler = new RequestHandler();
	        OlapRequest olapRequest = new OlapRequest(datasetUri, null, null, null);
			try {
				requestHandler.getOlapResult(olapRequest, true);
			} catch (OlapException e) {
				logger.log(Level.WARNING, "Error while reqeusting initRequest for datasetUri: " + datasetUri, e);
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
	}
	
	private void initLabelMap(HttpServletRequest req, HttpServletResponse resp){
		String dsId = req.getParameter(RequestParameter.DATASET_ID);
		URL dsUri = configManager.getDatasetUriOfId(dsId);
		if(dsUri == null){
			logger.log(Level.WARNING, "Failed to initLabelMap. Id could not be found: " + dsId);
			return;
		}
		
		RequestHandler requestHandler = new RequestHandler();
		try {
			requestHandler.getLabelMap(dsUri, true);
			logger.log(Level.INFO, "Reloaded labelMap of DatasetUri: "+ dsUri);
		} catch (OlapException e) {
			logger.log(Level.WARNING, "Error getting LabelMap of DatasetUri: "+ dsUri);
		}
		
	}
	
	private void initDatabase(HttpServletRequest req, HttpServletResponse resp){
		String dsId = req.getParameter(RequestParameter.DATASET_ID);
		URL dsUri = configManager.getDatasetUriOfId(dsId);
		if(dsUri == null){
			logger.log(Level.WARNING, "Failed to initDatabase. Id could not be found: " + dsId);
			return;
		}
		
		DatabaseManager dbManager = new DatabaseManager();
		dbManager.fillDatabaseWithDatasetRequests(dsUri);
	}
}
