package com.breucker.seo4olap.main;

import java.net.URL;
import java.util.List;

import com.breucker.seo4olap.server.RequestParameter;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class DatabaseManager {
	
	private final ConfigurationManager configManager;
	private final RequestHandler requestHandler;
	private final String baseUri;
	
	public DatabaseManager(){
		this.configManager = ConfigurationManagerFactory.getConfigurationManager();
		this.requestHandler = new RequestHandler();
		this.baseUri = configManager.getBaseUri();
	}

	public void fillDatabaseWithAllRequests(){
		List<URL> datasetsUris = configManager.getDatasetUris();
		for(URL datasetUri: datasetsUris){
			fillDatabaseWithDatasetRequests(datasetUri);
		}
	}
	
	public void fillDatabaseWithDatasetRequests(URL datasetUri){
		List<String> requests = requestHandler.getUrlRequestList(datasetUri, false, true);
		Queue queue = QueueFactory.getQueue("setup-database-queue");
		int size = requests.size();
		for(int i = 0; i < size; i++){
			String request = requests.get(i);
			//request Uri has to be relative 
			String relativeUri = getRelativeUri(request);
			if(relativeUri != null){
				queue.add(TaskOptions.Builder.withUrl(relativeUri).param(RequestParameter.RECOMPUTE, "true"));
			}
		}
	}
	
	private String getRelativeUri(String uri){
		String relativeUri = null;
		if(uri == null){
			return relativeUri;
		}
		if(uri.startsWith(baseUri)){
			String[] tokens = uri.split(baseUri);
			if(tokens.length > 1){
				relativeUri = tokens[1];
			}
		}
		return relativeUri;
	}
}
