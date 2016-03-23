package com.breucker.seo4olap.main;

import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.breucker.seo4olap.olap.OlapRequest;
import com.breucker.seo4olap.server.RequestParameter;

public class OlapRequestGenerator {

	private final ConfigurationManager configManager;
	private final URL datasetUri;
	
	public OlapRequestGenerator(URL datasetUri) {
		if(datasetUri == null){
			throw new InvalidParameterException("datasetUri cannot be null");
		}
		this.configManager = ConfigurationManagerFactory.getConfigurationManager();
		this.datasetUri = datasetUri;
	}
	
	
	/**
	 * Generate an OlapRequest
	 * @param measures2project UniqueNames of measures (can also include measureMembers)
	 * @param dimensions2keep UniqueNames of dimensions, that shall be kept/displayed
	 * @param members2dice UniqueNames of members, that shall be diced
	 * @return
	 */
	public OlapRequest generateOlapRequest(List<String> measures2project, List<String> dimensions2keep, List<String> members2dice){
		if(measures2project == null){
			measures2project = new ArrayList<String>();
		}
		if(dimensions2keep == null){
			dimensions2keep = new ArrayList<String>();
		}
		if(members2dice == null){
			members2dice = new ArrayList<String>();
		}
		
		List<String> requestMeasures = new ArrayList<String>();
		List<String> requestDimensions = new ArrayList<String>();
		List<String> requestMembers = new ArrayList<String>();
		
		//handle measures
		for(String measure : measures2project){
			if(configManager.isMeasureMember(datasetUri, measure)){
				//add parentMeasure of measureMember
				String parentMeasure = configManager.getParentMeasureOfMeasureMember(datasetUri, measure);
				if(parentMeasure == null){
					continue;
				}
				String measureMember = configManager.getMemberOfMeasure(datasetUri, measure);
				if(measureMember != null){
					requestMeasures.add(parentMeasure);
					requestMembers.add(measureMember);
				}
			}
			else{
				requestMeasures.add(measure);
			}
		}
		
		//handle members
		requestMembers.addAll(members2dice);
		
		//handle dimensions
		//Dimensions, that are not free (in members2keep) are sliced. Some dimensions have a sliceMember.
		//in that case, add this member to members2dice		
		List<String> configDimensions = configManager.getDimensions(datasetUri);
		if(configDimensions != null){
			for(String dimension : dimensions2keep){
				boolean addDimension = true;
				for(String diceMember: members2dice){
					//do not add dimension to freeDimensions if a member of this dimension is diceMember
					if(dimension.equals(configManager.getParent(datasetUri, diceMember))){
						addDimension = false;
					}
				}
				if(addDimension){
					requestDimensions.add(dimension);
					configDimensions.remove(dimension);
				}
			}
			
			for(String dimension : configDimensions){
				if(configManager.isMeasureDimension(datasetUri, dimension)){
					continue;
				}
				String sliceMember = configManager.getSliceMember(datasetUri, dimension);
				if(sliceMember != null){
					List<String> dimensionMembers = configManager.getMembers(datasetUri, dimension);
					//do not add this member if another member of this dimension is already selected for dicing
					boolean addSliceMember = true;
					for(String dimensionMember : dimensionMembers){
						for(String requestMember : requestMembers){
							if(dimensionMember.equals(requestMember)){
								addSliceMember = false;
							}
						}
					}
					if(addSliceMember){
						requestMembers.add(sliceMember);
					}
				}
			}
		}
		
		return new OlapRequest(datasetUri, requestMembers, requestDimensions , requestMeasures);
	}
	
	/**
	 * Generate an OlapRequest from a ParameterMap (can be used by Servlets)
	 * 
	 * @param requestParameterMap a Map with keys RequestParameter (DIMENSION, MEASURE, MEMBER) and 
	 * values String[] of IDs
	 * @return
	 */
	public OlapRequest generateOlapRequest(Map<String, String[]> requestParameterMap){
		if(requestParameterMap == null){
			throw new InvalidParameterException("requestParameterMap cannot be null");
		}
		
		List<String> measures2project = new ArrayList<String>();
		List<String> members2dice = new ArrayList<String>();
		List<String> dimensions2keep = new ArrayList<String>();
		
		String[] reqMeasures = requestParameterMap.get(RequestParameter.MEASURE);
		if(reqMeasures != null){
			for(String reqMeasure : reqMeasures){
				String reqMeasureUniqueName = configManager.getUniqueName(datasetUri, reqMeasure);
				if(reqMeasureUniqueName == null){
					continue;
				}
				measures2project.add(reqMeasureUniqueName);
			}
		}
		
		String[] reqDimensions = requestParameterMap.get(RequestParameter.DIMENSION);
		if(reqDimensions != null){
			for(String reqDimension : reqDimensions){
				String reqDimensionUniqueName = configManager.getUniqueName(datasetUri, reqDimension);
				if(reqDimensionUniqueName == null){
					continue;
				}
				dimensions2keep.add(reqDimensionUniqueName);
			}
		}
		
		String[] reqMembers = requestParameterMap.get(RequestParameter.MEMBER);
		if(reqMembers != null){
			for(String reqMember : reqMembers){
				String reqMemberUniqueName = configManager.getUniqueName(datasetUri, reqMember);
				if(reqMemberUniqueName == null){
					continue;
				}
				members2dice.add(reqMemberUniqueName);
			}
		}
		
		return generateOlapRequest(measures2project, dimensions2keep, members2dice);
	}

}
