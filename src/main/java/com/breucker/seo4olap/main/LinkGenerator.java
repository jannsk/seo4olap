package com.breucker.seo4olap.main;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.breucker.seo4olap.olap.OlapRequest;

class LinkGenerator {

	private static final ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
	
	private LinkGenerator() {}

	/**
	 * Returns a link to the specified path
	 * @param path: A String in valid path format. Format: baseuri/endpoint/pattern/ids
	 * @return a Link with (url and text), null if path was not valid
	 */
	public static Link getLink(String path){
		OlapRequest request = PathConverter.getOlapRequest(path);
		if(request == null){
			return null;
		}
		return getLink(request, path);
	}
	
	/**
	 * Returns a link to the specified olapRequest
	 * @param olapRequest: An olapRequest
	 * @return a Link with (url and text), null if olapRequest was not valid
	 */
	public static Link getLink(OlapRequest olapRequest, boolean absolutePath){
		if(olapRequest == null){
			return null;
		}
		String path = PathConverter.getPath(olapRequest, absolutePath);
		return getLink(olapRequest, path);
	}
	
	/**
	 * Returns a link to the specified Overview Page of the datasetUri
	 * @param datasetUri
	 * @return a Link with (url and text), null if olapRequest was not valid
	 */
	public static Link getDatasetLink(URL datasetUri, boolean absolutePath){
		Link link = null;
		if(datasetUri == null){
			return link;
		}
		OlapRequest olapRequest = new OlapRequest(datasetUri, null, null, null);
		String text = "Overview of Dataset: " + configManager.getDatasetTitle(datasetUri);
		String path = PathConverter.getPath(olapRequest, absolutePath);
		if(text != null && path != null){
			link = new Link(path, text);
		}
		return link;
	}
	
	/**
	 * Returns a list of Links, that are close to the current OlapRequest.
	 * @param olapRequest
	 * @return
	 */
	public static List<Link> getChangeViewLinks(OlapRequest olapRequest){
		if(olapRequest == null){
			return null;
		}
		final URL dsUri = olapRequest.getDatasetUri();		
		final List<String> requestMembers = olapRequest.getMembers2dice();
		List<Link> linkList = new ArrayList<Link>();

		//remove all dimensions that are already in dimensions2keep
		List<String> dimensions = configManager.getDimensions(dsUri);
		for(String requestDimension : olapRequest.getDimensions2keep()){
			dimensions.remove(requestDimension);
		}
		//remove all measureDimensions
		List<String> measureDimensions = new ArrayList<String>();
		for(String dimension : dimensions){
			if(configManager.isMeasureDimension(dsUri, dimension)){
				measureDimensions.add(dimension);
			}
		}
		//remove all Dimensions with an active diceMember
		dimensions.removeAll(measureDimensions);
		for(String member : requestMembers){
			dimensions.remove(configManager.getParent(dsUri, member));
		}
		
		linkList.addAll(getLinks(olapRequest, dimensions, IdType.DIMENSION, false));		
		
		return linkList;
	}
	
	
	
	private static List<Link> getLinks(OlapRequest olapRequest, List<String> ids, IdType type, boolean absolutePath){
		List<Link> linkList = new ArrayList<Link>();
		final URL dsUri = olapRequest.getDatasetUri();
		final List<String> requestMembers = olapRequest.getMembers2dice();
		final List<String> requestMeasures = olapRequest.getMeasures2project();
		final List<String> requestDimensions = olapRequest.getDimensions2keep();
		List<String> requestIds = null;
		if(type == IdType.DIMENSION){
			requestIds = requestDimensions;
		}
		if(type == IdType.MEASURE){
			requestIds = requestMeasures;
		}
		if(type == IdType.MEMBER){
			requestIds = requestMembers;
		}		
		
		//change for every id in requestIds the id with another one from Ids
		for(String requestId : requestIds){
			List<String> newIds = deepCopyList(requestIds);
			for(String id : ids){
				newIds.remove(requestId);
				newIds.add(id);
				
				Link link = null;
				if(type == IdType.DIMENSION){
					link = getLink(new OlapRequest(dsUri, requestMembers, newIds, requestMeasures), absolutePath);
				}
				if(type == IdType.MEASURE){
					link = getLink(new OlapRequest(dsUri, requestMembers, requestDimensions, newIds), absolutePath);
				}
				if(type == IdType.MEMBER){
					link = getLink(new OlapRequest(dsUri, newIds, requestDimensions, requestMeasures), absolutePath);
				}
				if(link != null){
					linkList.add(link);
				}
			}
		}	
		
		//add one more id, e.g. if requestedmembers were 0, then add one new one
		
		SitemapConfiguration sitemap = configManager.getSitemapConfiguration(dsUri);
		if(sitemap == null){
			sitemap = new SitemapConfiguration();
		}
		int configMaxDimensionCount = sitemap.getMaxDimensionCount();
		int configMaxMeasureCount = sitemap.getMaxMeasureCount();
		int configMaxMemberCount = sitemap.getMaxMemberCount();
		
		if(type == IdType.DIMENSION && requestIds.size() < configMaxDimensionCount){
			for(String id : ids){
				List<String> newIds = deepCopyList(requestIds);
				newIds.add(id);
				Link link = getLink(new OlapRequest(dsUri, requestMembers, newIds, requestMeasures), absolutePath);
				if(link != null){
					linkList.add(link);
				}
			}
		}
		
		if(type == IdType.MEASURE && requestIds.size() < configMaxMeasureCount){
			for(String id : ids){
				List<String> newIds = deepCopyList(requestIds);
				newIds.add(id);
				Link link = getLink(new OlapRequest(dsUri, requestMembers, requestDimensions, newIds), absolutePath);
				if(link != null){
					linkList.add(link);
				}
			}
		}
		
		if(type == IdType.MEMBER && requestIds.size() < configMaxMemberCount){
			for(String id : ids){
				List<String> newIds = deepCopyList(requestIds);
				newIds.add(id);
				Link link = getLink(new OlapRequest(dsUri, newIds, requestDimensions, requestMeasures), absolutePath);
				if(link != null){
					linkList.add(link);
				}
			}
		}		
		return linkList;
	}
	
	private static List<String> deepCopyList(List<String> array){
		List<String> list = new ArrayList<String>();
		if(array != null){
			for(String item : array){
				list.add(item);
			}
		}
		return list;
	}
	
	private static Link getLink(OlapRequest olapRequest, String path){
		if(olapRequest == null || path == null){
			return null;
		}
		ResultTextGenerator textGenerator = new ResultTextGenerator(olapRequest);
		String queryTitle = textGenerator.getQueryTitle();
		if(queryTitle.equals("")){
			queryTitle = path;
		}
		return new Link(path, queryTitle);
	}
	
	private enum IdType{
		MEASURE, MEMBER, DIMENSION
	}
}
