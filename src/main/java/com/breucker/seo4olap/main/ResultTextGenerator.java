package com.breucker.seo4olap.main;

import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.olap4j.OlapException;

import com.breucker.seo4olap.olap.OlapRequest;


/**
 * @author dbr
 *
 */
public class ResultTextGenerator {

	private static final Logger logger = Logger.getLogger(ResultTextGenerator.class.getName());
	private static final Map<String, Map<String, String>> labelMapStore = new HashMap<String, Map<String, String>>();
	
	private final OlapRequest olapRequest;
	private final URL dsUri;
	private final ConfigurationManager configManager;
	private final RequestHandler requestHandler;
	private final List<String> freeDimensions;
	private final List<String> slicedDimensions;
	private final Map<String, String> dicedMembers;
	private final List<String> projectedMeasures;
	private final Map<String, String> labelMap;
	
	public ResultTextGenerator(OlapRequest olapRequest) {
		if(olapRequest == null){
			throw new InvalidParameterException("olapRequest cannot be null");
		}
		this.olapRequest = olapRequest;
		this.dsUri = olapRequest.getDatasetUri();
		this.configManager = ConfigurationManagerFactory.getConfigurationManager();
		this.requestHandler = new RequestHandler();
		this.labelMap = getLabelMap();
		this.freeDimensions = initFreeDimensions();
		this.slicedDimensions = initSlicedDimensions();
		this.dicedMembers = initDicedMembers();
		this.projectedMeasures = initProjectedMeasures();
	}
	
	
	/**
	 * Get label corresponding to a uniqueName. If no label is found, the uniqueName is returned.
	 * @param uniqueName UniqueName of Member, Measure, Dimension or any other
	 * @return label corresponding to uniqueName, uniqueName if no label was found, null if uniqueName = null
	 */
	public String getLabel(String uniqueName){
		if(uniqueName == null){
			return null;
		}
		String label = this.labelMap.get(uniqueName);
		if(label == null){
			return uniqueName;
		}
		return label;
	}
	
	/**
	 * Get Filters of this olapRequest. Each filter is displayed with the pattern 'Dimension: Member'
	 * @return
	 */
	public String[] getFilters(){
		List<String> filters = new ArrayList<String>();
//		//get all diced Members
//		if(this.dicedMembers != null){
//			for(String memberUniqueName : this.dicedMembers.values()){
//				
//				String label = getLabel(memberUniqueName);
//				String dimensionUniqueName = configManager.getParent(dsUri, memberUniqueName);
//				String dimensionLabel = getLabel(dimensionUniqueName);
//				if(dimensionLabel == null){
//					filters.add(label);
//				} else{
//					filters.add(dimensionLabel + ": " + label);
//				}
//				
//			}		
//		}
		
		if(this.slicedDimensions != null){
			for(String dimensionUniqueName : this.slicedDimensions){
				
				String dimensionLabel = getLabel(dimensionUniqueName);
				//TODO concrete aggregation function
				String label = "Aggregation of all";
				if(dimensionLabel == null){
					dimensionLabel = dimensionUniqueName;
				} 
				//check if dimension has diced Member
				String member = this.dicedMembers.get(dimensionUniqueName);
				if(member == null){
					//check if Dimension has sliceMember
					member = configManager.getSliceMember(dsUri, dimensionUniqueName);
				}
				if(member != null){
					label = getLabel(member);
				}
				else{
					String sliceLabel = configManager.getSliceLabel(dsUri, dimensionUniqueName);
					if(sliceLabel != null){
						label = sliceLabel;
					}
				}
				filters.add(dimensionLabel + ": " + label);
			}		
		}
		return filters.toArray(new String[0]);
	}
	
	/**
	 * Get a String of comma-separated keywords
	 * @return
	 */
	public String getMetadataKeywords() {
		List<String> keywords = new ArrayList<String>();
		//create keywords with pattern: Measure + member
		for(String measureUniqueName : this.projectedMeasures){
			String measureLabel = getLabel(measureUniqueName);
			String concatItem = getConcatItem(ConcatType.OF);
			for(String memberUniqueName : this.dicedMembers.values()){
				String memberLabel = getLabel(memberUniqueName);
				if(memberLabel != null){
					keywords.add(measureLabel + " " + concatItem + " " + memberLabel);
				}
			}
			if(dicedMembers.size() == 0 || dicedMembers.size() == 1){
				//if we do not stop here, alleMembers would result in a duplication of keywords
				continue;
			}
			String allMembers = getConcatenatedString(dicedMembers.values(), getConcatItem(ConcatType.AND), true);
			if(allMembers != null){
				keywords.add(measureLabel + " " + concatItem + " " + allMembers);
			}
		}
		for(String measure : this.projectedMeasures){
			String label = getLabel(measure);
			if(label != null){
				keywords.add(label);
			}
		}
		for(String dimension : this.freeDimensions){
			String label = getLabel(dimension);
			if(label != null){
				keywords.add(label);
			}
		}
		for(String member : this.dicedMembers.values()){
			String label = getLabel(member);
			if(label != null){
				keywords.add(label);
			}
		}
		String metadataKeywords = getConcatenatedString(keywords, ", ", false);
		return metadataKeywords;
	}
	
	
	/** 
	 * Get a title for use in metadata fields (html)
	 * @return
	 */
	public String getMetadataTitle() {
		String datasetTitle = configManager.getDatasetTitle(dsUri);
		String queryTitle = getQueryTitle();
		return queryTitle + " - " + datasetTitle;
	}
	
	
	/**
	 * Get the query title, pattern: Measure of member per dimension
	 * @return
	 */
	public String getQueryTitle() {
		String measures = getConcatenatedString(this.projectedMeasures, getConcatItem(ConcatType.AND), true);
		String dimensions = getConcatenatedString(this.freeDimensions, getConcatItem(ConcatType.AND), true);
		String members = getConcatenatedString(this.dicedMembers.values(), getConcatItem(ConcatType.AND), true);
		
		String queryTitle = "";
		if(measures == null){
			return "";
		}
		queryTitle += measures;
		if(members != null){
			queryTitle += " " + getConcatItem(ConcatType.OF) + " " + members;
		}
		if(dimensions != null){
			queryTitle += " " + getConcatItem(ConcatType.PER) + " " + dimensions;
		}
		return queryTitle;
	}
	
	/**
	 * Returns the datasetDescription from Configuration
	 * @return
	 */
	public String getDatasetDescription() {
		return configManager.getDatasetDescription(dsUri);
	}
	
	/**
	 * Returns description for HTML-Metadata
	 * @return
	 */
	public String getMetadataDescription() {
		return getQueryTitle() + ". Description of the dataset: " + getDatasetDescription();
	}
	
	/**
	 * Returns a list of uniqueNames of Members, that are actually diced (filtered). 
	 * That means, that sliceMembers and measureMembers are not listed
	 * @return list of uniqueNames of members
	 */
	public List<String> getDicedMembers(){
		List<String> dicedMembers = new ArrayList<String>();
		dicedMembers.addAll(this.dicedMembers.values());
		return dicedMembers;
	}
	
	/**
	 * Returns a list of uniqueNames of Dimensions, i.e. those dimensions that are free (not filtered by one member)
	 * @return list of uniqueNames of free Dimensions
	 */
	public List<String> getFreeDimensions(){
		return this.freeDimensions;
	}
	
	
	/**
	 * Returns a list of uniqueNames of projected Measures
	 * @return list of uniqueNames of projected Measures
	 */
	public List<String> getProjectedMeasures(){
		return this.projectedMeasures;
	}
	
	/*#############------------####################
	 * 
	 * Private Methods
	 * 
	 *#############------------####################*/
	
	private List<String> initFreeDimensions() {
		//treat measureDimension and SliceMembers
		List<String> freeDims = new ArrayList<String>();
		List<String> requestDimensions = olapRequest.getDimensions2keep();
		for(String requestDimension : requestDimensions){
			if(!configManager.isMeasureDimension(dsUri, requestDimension) && !isDiceMemberPartOfDimension(requestDimension)){
				freeDims.add(requestDimension);
			}
		}
		return freeDims;
	}
	
	private List<String> initSlicedDimensions() {
		//get all dimensions, that are not free
		List<String> slicedDims = new ArrayList<String>();
		configManager.getDimensions(dsUri);
		for(String configDimension : configManager.getDimensions(dsUri)){
			if(!configManager.isMeasureDimension(dsUri, configDimension) && !isFreeDimension(configDimension)){
				slicedDims.add(configDimension);
			}
		}
		return slicedDims;
	}
	
	/**
	 * Check if Dimension is among freeDimensions. Only call after initFreeDimensions() has been called.
	 * @param dimensionUniqueName
	 * @return
	 */
	private boolean isFreeDimension(String dimensionUniqueName){
		boolean result = false;
		for(String dimension : this.freeDimensions){
			if(dimension.equals(dimensionUniqueName)){
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * Check if one of the DiceMembers has requested Dimension as Parent
	 * @param dimensionUniqueName
	 * @return
	 */
	private boolean isDiceMemberPartOfDimension(String dimensionUniqueName){
		boolean result = false;
		List<String> members = olapRequest.getMembers2dice();
		for(String member : members){
			String parent = configManager.getParent(dsUri, member); 
			if(parent != null && parent.equals(dimensionUniqueName)){
				result = true;
			}
		}
		return result;
	}
	
	private List<String> initProjectedMeasures() {
		List<String> projMeasures = new ArrayList<String>();
		List<String> measures2project = olapRequest.getMeasures2project();
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
		
		
		
		for(String measure : measures2project){
			List<String> measureMembers = configManager.getMembers(dsUri, measure);
			if(measureMembers == null || measureMembers.isEmpty()){
				//normal Measure, no measureMembers
				projMeasures.add(measure);
				continue;
			}
			for(String member2dice : members2dice){
				for(String measureMember : measureMembers){
					String memberUniqueName = configManager.getMemberOfMeasure(dsUri, measureMember);
					String parentMeasure = configManager.getParentMeasureOfMeasureMember(dsUri, measureMember);
					if(member2dice.equals(memberUniqueName)){
						if(parentMeasure != null){
							projMeasures.add(parentMeasure);
						}
					}
				}
			}
		}
		return projMeasures;
	}


	private Map<String, String> initDicedMembers() {
		Map<String, String> diMembers = new HashMap<String, String>();
		for(String member : olapRequest.getMembers2dice()){
			String parent = configManager.getParent(dsUri, member);
			diMembers.put(parent, member);
			
			//remove member if it is from a MeasureDimension
			if(configManager.isMeasureDimension(dsUri, parent)){
				diMembers.remove(parent);
			}
			
			//remove sliceMembers
			if(configManager.isSliceMember(dsUri, member)){
				diMembers.remove(parent);
			}
		}
		return diMembers;
	}
	
	/**
	 * Returns a Map with key: uniqueName of Dimensions/Measure/Member and 
	 * value: corresponding label.
	 * If the Configuration has a label, then this is preferred to the one specified in the dataset.
	 * @return
	 */
	private Map<String, String> getLabelMap() {
		//get Map containing all labels from Dataset
		Map<String, String> labelMap = null;
		
		labelMap = labelMapStore.get(dsUri.toString());
		if(labelMap != null){
			return labelMap;
		}
		try {
			labelMap = requestHandler.getLabelMap(dsUri, false);
		} catch (OlapException e) {
			logger.log(Level.SEVERE, "Failed getting LabelMap from OlapHandler", e);
		}
		
		Map<String, String> configLabelMap = configManager.getLabelMap(dsUri);
		
		if(labelMap != null){
			//update Map with all Labels from Configuration
			labelMap.putAll(configLabelMap);
		}
		else{
			labelMap = configLabelMap;
		}
		labelMapStore.put(dsUri.toString(), labelMap);
		return labelMap;
	}
	
	//can later be implemented for Multilanguage use
	private String getConcatItem(ConcatType type){
		if(type == ConcatType.AND){
			return " and ";
		}
		if(type == ConcatType.OF){
			return " of ";
		}
		if(type == ConcatType.PER){
			return " per ";
		}
		return " ";
	}
		
	private String getConcatenatedString(Collection<String> strings, String concatItem, boolean checkForLabels){
		String result = null;
		if(strings == null || concatItem == null){
			return result;
		}
		for(String uniqueName : strings){
			if(uniqueName == null){
				continue;
			}
			String label = null;
			if(checkForLabels){
				label = getLabel(uniqueName);
			}
			else{
				label = uniqueName;
			}
			if(result == null){
				result = label;
			}
			else{
				result += concatItem + label;
			}
		}
		if(result != null){
			result.trim();
		}
		return result;
	}
	
	private enum ConcatType{
		OF, AND, PER
	}
	
}
