package com.breucker.seo4olap.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.breucker.seo4olap.olap.OlapRequest;

class PathConverter {

	private static ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
	
	private PathConverter() {}

	/**
	 * Get a path corresponding to the olapRequest<br>
	 * A Path has the following format:<br>
	 * path: baseUri/endpoint/pattern/id/id/id... with:<br>
	 * - baseUri: e.g. http://example.org<br>
	 * - endpoint: e.g example<br>
	 * - pattern: e.g. 122<br>
	 * - id: e.g. dates
	 * @param olapRequest
	 * @return the path, null if olapRequest did not match with DatasetConfiguration
	 */
	public static String getPath(OlapRequest olapRequest, boolean absolutePath){
		if(olapRequest == null){
			return null;
		}
		URL dsUri = olapRequest.getDatasetUri();
		String baseUri = configManager.getBaseUri();
		String endpoint = configManager.getDatasetEndpoint(dsUri);
		if(dsUri == null || baseUri == null || endpoint == null){
			return null;
		}
		List<String> measures2project = olapRequest.getMeasures2project();
		List<String> dimensions2keep = olapRequest.getDimensions2keep();
		List<String> members2dice = olapRequest.getMembers2dice();
		
		
		//preprocess measures with measureMembers
		List<String> measures2remove = new ArrayList<String>();
		List<String> measures2add = new ArrayList<String>();
		List<String> members2remove = new ArrayList<String>();
		
		for(String member2dice: members2dice){
			if(configManager.isMeasureDimension(dsUri, configManager.getParent(dsUri, member2dice))){
				for(String measure: measures2project){
					List<String> measureMembers = configManager.getMembers(dsUri, measure);
					if(measureMembers == null || measureMembers.isEmpty()){
						continue;
					}
					for(String measureMember: measureMembers){
						String correspondingMember = configManager.getMemberOfMeasure(dsUri, measureMember);
						if(member2dice.equals(correspondingMember)){
							members2remove.add(member2dice);
							measures2remove.add(measure);
							measures2add.add(measureMember);
						}
					}
				}
			}
		}
		measures2project.removeAll(measures2remove);
		measures2project.addAll(measures2add);
		members2dice.removeAll(members2remove);
		
		
		//generate Path
		String path = "";
		if(absolutePath){
			path += baseUri;
		}
		path += "/" + endpoint;
		int measureCount = measures2project.size();
		int dimensionCount = dimensions2keep.size();
		int memberCount = members2dice.size();
		
		String pattern = "";
		pattern += measureCount;
		pattern += dimensionCount;
		pattern += memberCount;
		path += "/" + pattern;
		
		//check if pattern is allowed
		if(!isValid(dsUri, measureCount, dimensionCount, memberCount)){
			return null;
		}
		
		String ids = "";
		List<String> measures = olapRequest.getMeasures2project();
		Collections.sort(measures);
		for(String uniqueName : measures){
			String id = configManager.getId(dsUri, uniqueName);
			if(id != null){
				ids += "/" + id;
			}
		}
		List<String> dimensions = olapRequest.getDimensions2keep();
		Collections.sort(dimensions);
		for(String uniqueName : dimensions){
			String id = configManager.getId(dsUri, uniqueName);
			if(id != null){
				ids += "/" + id;
			}
		}
		List<String> members = olapRequest.getMembers2dice();
		Collections.sort(members);
		for(String uniqueName : members){
			String id = configManager.getId(dsUri, uniqueName);
			if(id != null){
				ids += "/" + id;
			}
		}
		
		path += ids;
		
		return path;
	}
	
	
	/**
	 * Get an OlapRequest from a path
	 * A Path needs to have the following format:<br>
	 * path: baseUri/endpoint/pattern/id/id/id... with:<br>
	 * - baseUri: e.g. http://example.org<br>
	 * - endpoint: e.g example<br>
	 * - pattern: e.g. 122<br>
	 * - id: e.g. dates
	 * @param path
	 * @return an OlapRequest matching the path, null if path did not match format
	 */
	public static OlapRequest getOlapRequest(String path){
		
		if(path == null ){
			return null;
		}
		String baseUri = configManager.getBaseUri();
		
		String[] base = path.split(baseUri);
		String pathWithoutBaseUri;
		if(base.length == 2){
			pathWithoutBaseUri = base[1];
		}
		else if(base.length == 1){
			pathWithoutBaseUri = base[0];
		}
		else{
			return null;
		}
		List<String> tokens = new ArrayList<String>();
		for(String item : pathWithoutBaseUri.split("/")){
			if(!item.equals("")){
				tokens.add(item);
			}
		}
		if(tokens.isEmpty()){
			return null;
		}
		
		String endpoint = tokens.remove(0);
		URL datasetUri = configManager.getDatasetUriOfEndpoint(endpoint);
		String pattern = tokens.remove(0);
		if(datasetUri == null || pattern == null || pattern.length() != 3){
			return null;
		}
		String[] patternTokens = pattern.split("");
		Integer maxMeasureCount;
		Integer maxDimensionCount;
		Integer maxMemberCount;
		try{
			maxMeasureCount = Integer.valueOf(patternTokens[1]);
			maxDimensionCount = Integer.valueOf(patternTokens[2]);
			maxMemberCount = Integer.valueOf(patternTokens[3]);
		} catch(Exception e){
			return null;
		}
		
		List<String> measures2project = new ArrayList<String>();
		List<String> dimensions2keep = new ArrayList<String>();
		List<String> members2dice = new ArrayList<String>();
		
		for(int i = 0; i < maxMeasureCount; i++){
			if(tokens.isEmpty()){
				continue;
			}
			String id = tokens.remove(0);
			String uniqueName = configManager.getUniqueName(datasetUri, id);
			if(uniqueName != null){
				measures2project.add(uniqueName);
			}
		}
		
		for(int i = 0; i < maxDimensionCount; i++){
			if(tokens.isEmpty()){
				continue;
			}
			String id = tokens.remove(0);
			String uniqueName = configManager.getUniqueName(datasetUri, id);
			if(uniqueName != null){
				dimensions2keep.add(uniqueName);
			}
		}
		
		for(int i = 0; i < maxMemberCount; i++){
			if(tokens.isEmpty()){
				continue;
			}
			String id = tokens.remove(0);
			String uniqueName = configManager.getUniqueName(datasetUri, id);
			if(uniqueName != null){
				members2dice.add(uniqueName);
			}
		}
		
		OlapRequest olapRequest = new OlapRequestGenerator(datasetUri).
				generateOlapRequest(measures2project, dimensions2keep, members2dice);
		return olapRequest;
	}
	
	private static boolean isValid(URL dsUri, int measureCount, int dimensionCount, int memberCount){
		
		//special case for pattern 000
		if(dsUri != null && measureCount == 0 && dimensionCount == 0 && memberCount == 0){
			return true;
		}
		
		SitemapConfiguration sitemap = configManager.getSitemapConfiguration(dsUri);
		if(sitemap == null){
			//take default
			sitemap = new SitemapConfiguration();					
		}
		
		boolean areMeasuresValid = measureCount >= sitemap.getMinMeasureCount() && measureCount <= sitemap.getMaxMeasureCount();
		boolean areDimensionsValid = dimensionCount >= sitemap.getMinDimensionCount() && dimensionCount <= sitemap.getMaxDimensionCount();
		boolean areMembersValid = memberCount >= sitemap.getMinMemberCount() && memberCount <= sitemap.getMaxMemberCount();
		
		return areMeasuresValid && areDimensionsValid && areMembersValid;
	}

}
