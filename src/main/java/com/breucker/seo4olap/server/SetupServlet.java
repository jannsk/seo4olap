package com.breucker.seo4olap.server;

import java.io.IOException;
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
import com.breucker.seo4olap.main.RuntimeContext;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;


@SuppressWarnings("serial")
public class SetupServlet extends HttpServlet {
	
	private static final Logger logger = Logger.getLogger(SetupServlet.class.getName());
	private final ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
	
	public void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		resp.setContentType("text/html");
		
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = req.getParameterMap();
		
		logger.log(Level.INFO, "Incoming Request. RequestParameter: " + ServerUtils.parameterMapToString(parameterMap));
		
		String task = req.getParameter(RequestParameter.TASK);
		if(task == null){
			return;
		}
		if(task.equals("setupDatabase")){
			setupDatabase(req, resp);
		}
		if(task.equals("setupLabelMap")){
			setupLabelMap(req, resp);
		}
		if(task.equals("setupDebug")){
			setupDebug(req, resp);
		}
		if(task.equals("resetStoredData")){
			resetStoredData(req, resp);
		}
	}
	
	private void setupDebug(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String query = req.getParameter(RequestParameter.DEBUG);
		if(query != null){
			RequestHandler requestHandler = new RequestHandler();
			RuntimeContext context = requestHandler.getRuntimeContext(false);
			boolean isDebugBefore = context.isDebugMode();
			if(query.equals("on")){
				context.setDebugMode(true);
				resp.getWriter().println("DebugMode set to: true" + "<br>");
			}
			else if(query.equals("off")){
				context.setDebugMode(false);
				resp.getWriter().println("DebugMode set to: false" + "<br>");
			}
			else{
				resp.getWriter().println("Wrong request. Call debug: off/on" + "<br>");
			}
			if(isDebugBefore != context.isDebugMode()){
				requestHandler.setRuntimeContext(context);
			}
		}
	}
	
	private void setupLabelMap(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String dsId = req.getParameter(RequestParameter.DATASET_ID);
		URL dsUri = configManager.getDatasetUriOfId(dsId);
		if(dsUri == null && !dsId.equals("all")){
			resp.getWriter().println("id could not be found" + "<br>");
			return;
		}
		
		Queue queue = QueueFactory.getQueue("init-queue");
		
		if(dsId.equals("all")){
			List<URL> datasetsUris = configManager.getDatasetUris();
			for(URL datasetUri: datasetsUris){
				queue.add(TaskOptions.Builder.withUrl("/admin/init").param(RequestParameter.DATASET_ID, configManager.getDatasetId(datasetUri))
						  .param(RequestParameter.TASK, "initLabelMap"));
				resp.getWriter().println("LabelMap initialization started for datasetUri: " + datasetUri + "<br>");
			}
		}
		else{
			queue.add(TaskOptions.Builder.withUrl("/admin/init").param(RequestParameter.DATASET_ID, dsId)
					  .param(RequestParameter.TASK, "initLabelMap"));
			resp.getWriter().println("LabelMap initialization started for dsId: " + dsId + "<br>");
		}
		
	}
	
	private void setupDatabase(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String dsId = req.getParameter(RequestParameter.DATASET_ID);
		URL dsUri = configManager.getDatasetUriOfId(dsId);
		if(dsUri == null && !dsId.equals("all")){
			resp.getWriter().println("id could not be found" + "<br>");
			return;
		}
		
		Queue queue = QueueFactory.getQueue("init-queue");
		
		if(dsId.equals("all")){
			List<URL> datasetsUris = configManager.getDatasetUris();
			for(URL datasetUri: datasetsUris){
				queue.add(TaskOptions.Builder.withUrl("/admin/init").param(RequestParameter.DATASET_ID, configManager.getDatasetId(datasetUri))
						  .param(RequestParameter.TASK, "initDatabase"));
				resp.getWriter().println("Database initialization started for datasetUri: " + datasetUri + "<br>");
			}
		}
		else{
			queue.add(TaskOptions.Builder.withUrl("/admin/init").param(RequestParameter.DATASET_ID, dsId)
					  .param(RequestParameter.TASK, "initDatabase"));
			resp.getWriter().println("Database initialization started for dsId: " + dsId + "<br>");
		}
	}
	
	private void resetStoredData(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		RequestHandler requestHandler = new RequestHandler();
		for(URL datasetUri: configManager.getDatasetUris()){
			requestHandler.getUrlRequestList(datasetUri, true, true);
			requestHandler.getUrlRequestList(datasetUri, true, false);
			resp.getWriter().println("RequestLists for datasetUri: " + datasetUri + " recomputed" + "<br>");
		}
		requestHandler.getIndexBean(true);
		resp.getWriter().println("StartSite recomputed" + "<br>");
		requestHandler.getRuntimeContext(true);
		resp.getWriter().println("RuntimeContext reseted" + "<br>");
	}
}
