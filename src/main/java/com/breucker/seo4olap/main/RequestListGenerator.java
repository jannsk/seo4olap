package com.breucker.seo4olap.main;

import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.breucker.seo4olap.olap.OlapRequest;

class RequestListGenerator {

	private final ConfigurationManager configManager;
	private final SitemapConfiguration sitemap;
	private List<Request> requests;
	private final URL datasetUri;
	private final List<String> measures;
	private final List<String> dimensions;
	private final List<String> members;
	
	public RequestListGenerator(URL datasetUri) {
		if(datasetUri == null){
			throw new InvalidParameterException("datasetUri cannot be null");
		}
		this.datasetUri = datasetUri;
		this.configManager = ConfigurationManagerFactory.getConfigurationManager();
		SitemapConfiguration sc = configManager.getSitemapConfiguration(datasetUri);
		if(sc == null){
			sc = new SitemapConfiguration();
		}
		this.sitemap = sc;
		this.requests = new ArrayList<Request>();

		//get Measures
		List<String> measures = configManager.getMeasures(datasetUri);
		List<String> measures2add = new ArrayList<String>();
		List<String> measures2remove = new ArrayList<String>();
		for(String measure : measures){
			List<String> measureMembers = configManager.getMembers(datasetUri, measure);
			if(measureMembers != null){
				measures2add.addAll(measureMembers);
				measures2remove.add(measure);
			}
		}
		measures.addAll(measures2add);
		measures.removeAll(measures2remove);
		this.measures = measures;
		
		//get Dimensions
		List<String> dimensions = configManager.getDimensions(datasetUri);
		List<String> dimensions2remove = new ArrayList<String>();
		for(String dimension: dimensions){
			if(configManager.isMeasureDimension(datasetUri, dimension)){
				dimensions2remove.add(dimension);
			}
		}
		dimensions.removeAll(dimensions2remove);
		this.dimensions = dimensions;
		
		//get Members without SliceMembers
		List<String> members = new ArrayList<String>();
		for(String dimension : dimensions){
			List<String> dimensionMembers = configManager.getMembers(datasetUri, dimension);
			if(dimensionMembers != null){
				for(String member: dimensionMembers){
					if(!configManager.isSliceMember(datasetUri, member)){
						members.add(member);
					}
				}
			}
		}
		this.members = members;		
	}
	
	/**
	 * Get a list of URL-Requests, e.g. baseUri/endpoint/pattern/id, given the Configuration of the Dataset
	 * @return
	 */
	public List<String> getURLRequestList(boolean absolutePath){
		List<String> requests = new ArrayList<String>();
		List<OlapRequest> olapRequests = getOlapRequestList();
		for(OlapRequest olapRequest: olapRequests){
			Link link = LinkGenerator.getLink(olapRequest, absolutePath);
			if(link != null && link.getUrl() != null){
				requests.add(link.getUrl());
			}
			
		}
		return requests;
	}

	/*#############------------####################
	 * 
	 * Private Methods
	 * 
	 *#############------------####################*/
	
	/**
	 * Get a list of OlapRequests, given the Configuration of the Dataset
	 * @return
	 */
	private List<OlapRequest> getOlapRequestList(){
		RequestPattern maxPattern = new RequestPattern(sitemap.getMaxMeasureCount(), 
				sitemap.getMaxDimensionCount(), sitemap.getMaxMemberCount());
		List<Request> rootList = this.generateRootList(maxPattern);
		for(Request root : rootList){
			this.doNextStep(root);
		}
		cleanup(requests);
		List<OlapRequest> olapRequests = new ArrayList<OlapRequest>();
		for(Request request: requests){
			olapRequests.add(request.getOlapRequest());
		}
		return olapRequests;
	}
	
	/**
	 * Recursive generation of further Request-Objects. Stops when all Requests are created.
	 * @param request
	 */
	private void doNextStep(Request request){
		RequestPattern actualPattern = request.actualPattern;
		RequestPattern targetPattern = request.targetPattern;
		if(actualPattern.equals(targetPattern)){
			requests.add(request);
			return;
		}
		if(actualPattern.amountMeasures < targetPattern.amountMeasures){
			addMetadata(request, this.measures, MetadataType.MEASURE);
			return;
		}
		if(actualPattern.amountDimensions < targetPattern.amountDimensions){
			addMetadata(request, this.dimensions, MetadataType.DIMENSION);
			return;
		}
		if(actualPattern.amountMembers < targetPattern.amountMembers){
			addMetadata(request, this.members, MetadataType.MEMBER);
			return;
		}
	}	
	
	/**
	 * Adds a Metadata Object to request, i.e. either a new Measure, a Dimension or a Member. Is called recursively 
	 * by doNextStep()
	 * @param request to add Metadata
	 * @param metadata uniqueName of Measure/Dimension/Member
	 * @param type MetadataType has to fit with metadata uniqueName
	 */
	private void addMetadata(Request request, List<String> metadata, MetadataType type){
		ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
		
		for(String uniqueName: metadata){
			Request newRequest = request.copy();
			String dimensionUniqueName = null;
			if(type.equals(MetadataType.DIMENSION)){
				dimensionUniqueName = uniqueName;
			}
			if(type.equals(MetadataType.MEMBER)){
				dimensionUniqueName = configManager.getParent(datasetUri, uniqueName);
			}
			//a path may only contain one member or dimension per dimension
			//a path may not include the same path item twice
			if(!newRequest.isIncrementIncluded(uniqueName) && !newRequest.hasDimension(dimensionUniqueName))
			{				
				newRequest.addToRequest(uniqueName, type);
				newRequest.increment(type);
				if(dimensionUniqueName != null){
					newRequest.putDimension(dimensionUniqueName);
				}
				doNextStep(newRequest);
			}
			else{
				newRequest = null;
			}
		}
	}
		
	/**
	 * Generates a rootList to start recursive Request-Creation
	 * @param maxPattern The maximal pattern to be included in PathList
	 * @return
	 */
	private List<Request> generateRootList(RequestPattern maxPattern){
		List<Request> rootList = new ArrayList<Request>();
		for(int amountMeasures = 0; amountMeasures <= maxPattern.amountMeasures; amountMeasures++){
			for(int amountDimensions = 0; amountDimensions <= maxPattern.amountDimensions; amountDimensions++){
				for(int amountMembers = 0; amountMembers <= maxPattern.amountMembers; amountMembers++){

					RequestPattern actualPattern = new RequestPattern(0,0,0);
					RequestPattern targetPattern = new RequestPattern(amountMeasures, amountDimensions, amountMembers);			
					OlapRequest olapRequest = new OlapRequest(datasetUri, new ArrayList<String>(), 
							new ArrayList<String>(), new ArrayList<String>());
					Request path = new Request(olapRequest, actualPattern, targetPattern, new HashSet<String>());
					rootList.add(path);
				}
			}
		}
		return rootList;
	}	
	
	/**
	 * Removes duplicate request. Duplicate request are e.g.
	 * 'mypath/path1/path2' and 'mypath/path2/path1'
	 * @param requests List of requests, in which duplicates should be removed
	 */
	private List<Request> removeDuplicates(List<Request> requests){
		Set<Integer> pathHashSet = new HashSet<Integer>();
		List<Request> paths2remove = new ArrayList<Request>();
		for(Request request : requests){
			boolean isUnique = pathHashSet.add(request.requestHash());
			if(!isUnique){
				paths2remove.add(request);
			}
		}
		requests.removeAll(paths2remove);
		return requests;
	}
	
	/**
	 * Cleans List of requests from duplicates and removes all requests, that do not fit for 
	 * the MinPattern from Configuration
	 * @param requests List of requests to clean
	 * @return cleaned List
	 */
	private List<Request> cleanup(List<Request> requests){
		requests = removeDuplicates(requests);
		RequestPattern minPattern = new RequestPattern(sitemap.getMinMeasureCount(), 
				sitemap.getMinDimensionCount(), sitemap.getMinMemberCount());
		List<Request> requests2remove = new ArrayList<Request>();
		for(Request request: requests){
			if(request.targetPattern.isSmaller(minPattern)){
				requests2remove.add(request);
			}
		}
		requests.removeAll(requests2remove);
		return requests;
	}

	
	
	/*#############------------####################
	 * 
	 * Inner classes
	 * 
	 *#############------------####################*/
	
	private class Request{

		private OlapRequest olapRequest;
		private RequestPattern actualPattern;
		private final RequestPattern targetPattern;
		private final Set<String> includedDimensions;
 
		public Request(OlapRequest olapRequest, RequestPattern actualPattern, RequestPattern targetPattern, Set<String> includedDimensions) {
			this.olapRequest = olapRequest;
			this.actualPattern = actualPattern;
			this.targetPattern = targetPattern;
			this.includedDimensions = includedDimensions;
		}

		public OlapRequest getOlapRequest() {
			return olapRequest;
		}
		
		public Request copy(){
			Set<String> newDimensions = new HashSet<String>();
			Iterator<String> it = this.includedDimensions.iterator();
			while(it.hasNext()){
				newDimensions.add(it.next());
			}
			return new Request(this.olapRequest.copy(), this.actualPattern.copy(), this.targetPattern.copy(), newDimensions);
		}
		
		public int requestHash(){
			return olapRequest.hashCode();
		}
		
		public void addToRequest(String uniqueName, MetadataType type){
			if(uniqueName != null){
				List<String> measures = olapRequest.getMeasures2project();
				List<String> dimensions = olapRequest.getDimensions2keep();
				List<String> members = olapRequest.getMembers2dice();
				if(type == MetadataType.MEASURE){
					measures.add(uniqueName);
				}
				if(type == MetadataType.DIMENSION){
					dimensions.add(uniqueName);
				}
				if(type == MetadataType.MEMBER){
					members.add(uniqueName);
				}
				OlapRequest newRequest = new OlapRequest(this.olapRequest.getDatasetUri(), 
						members, dimensions, measures);
				this.olapRequest = newRequest;
			}
		}
		
		public void putDimension(String dimensionUniqueName){
			includedDimensions.add(dimensionUniqueName);
		}
		
		public boolean hasDimension(String dimensionUniqueName){
			return includedDimensions.contains(dimensionUniqueName);
		}
		
		public String toString(){
			String output = "olapRequest:'" + olapRequest + "', actualPattern:'" + actualPattern.toString() 
			+ "', targetPattern:'" + targetPattern.toString() + "'"; 
			return output;
		}
		
		public void increment(MetadataType type){
			switch (type){
				case MEASURE: 
					RequestPattern newActualPattern = new RequestPattern(actualPattern.amountMeasures + 1, 
							actualPattern.amountDimensions, actualPattern.amountMembers);
					this.actualPattern = newActualPattern;
					break;
				case DIMENSION: 
					RequestPattern newActualPattern1 = new RequestPattern(actualPattern.amountMeasures, 
							actualPattern.amountDimensions + 1, actualPattern.amountMembers);
					this.actualPattern = newActualPattern1;
					break;
				case MEMBER: 
					RequestPattern newActualPattern2 = new RequestPattern(actualPattern.amountMeasures, 
							actualPattern.amountDimensions, actualPattern.amountMembers + 1);
					this.actualPattern = newActualPattern2;
					break;
			}
		}
		
		public boolean isIncrementIncluded(final String uniqueName){
			if(uniqueName == null){
				return false;
			}
			boolean isInMeasures = olapRequest.getMeasures2project().contains(uniqueName);
			boolean isInDimensions = olapRequest.getDimensions2keep().contains(uniqueName);
			boolean isInMembers = olapRequest.getMembers2dice().contains(uniqueName);
			
			return isInMeasures || isInDimensions || isInMembers ;
		}
		
		
	}	
	
	private class RequestPattern{
		
		public final int amountMeasures;
		public final int amountDimensions;
		public final int amountMembers;
		
		public RequestPattern(int amountMeasures, int amountDimensions, int amountMembers){
			this.amountMeasures = amountMeasures;
			this.amountDimensions = amountDimensions;
			this.amountMembers = amountMembers;
		}
		
		public RequestPattern copy(){
			return new RequestPattern(this.amountMeasures, this.amountDimensions, this.amountMembers);
		}
		
		public String toString(){
			String output = "";
			output += amountMeasures;
			output += amountDimensions;
			output += amountMembers;
			return output; 
		}
		
		public boolean equals(RequestPattern comparePattern){
			if(comparePattern == null){
				return false;
			}
			return (amountMeasures == comparePattern.amountMeasures &&  
					amountDimensions == comparePattern.amountDimensions &&
					amountMembers == comparePattern.amountMembers);
		}
		
		public boolean isSmaller(RequestPattern comparePattern){
			if(comparePattern == null){
				return false;
			}
			return (amountMeasures < comparePattern.amountMeasures ||  
					amountDimensions < comparePattern.amountDimensions ||
					amountMembers < comparePattern.amountMembers);
		}
	}
	
	enum MetadataType {
	    MEASURE, DIMENSION, MEMBER
	}
}
