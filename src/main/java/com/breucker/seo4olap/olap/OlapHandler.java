package com.breucker.seo4olap.olap;

import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.olap4j.OlapException;
import org.olap4j.driver.olap4ld.helper.Olap4ldLinkedDataUtil;
import org.olap4j.driver.olap4ld.linkeddata.BaseCubeOp;
import org.olap4j.driver.olap4ld.linkeddata.LogicalOlapOp;
import org.olap4j.driver.olap4ld.linkeddata.LogicalOlapQueryPlan;
import org.olap4j.driver.olap4ld.linkeddata.ProjectionOp;
import org.olap4j.driver.olap4ld.linkeddata.Restrictions;
import org.olap4j.driver.olap4ld.linkeddata.SliceOp;
import org.openrdf.model.Model;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;

import com.breucker.seo4olap.olap4ld.OlapSesameEngine;

/**
 * OlapHandler manages Connection with LinkedDataCubesEngine. It allows to retrieve OlapResults
 * @author Daniel Breucker
 */
public class OlapHandler{
	
//	private static final Logger logger = Logger.getLogger(OlapHandler.class.getName());	
	private final String dsUri;
	private final OlapSesameEngine lde;
	private final List<Node[]> members;
	private final List<Node[]> dimensions;
	private final List<Node[]> measures;
	private final List<Node[]> datasetInformation;
	private final Map<String, String> resultHeaderNames;
	private final Integer dimensionUniqueNameField;
	private final Integer dimensionCaptionField;
	private final Integer dimensionDescriptionField;
	private final Integer measureUniqueNameField;
	private final Integer measureCaptionField;
	private final Integer measureIsVisibleField;
	private final Integer measureAggregatorField;
	private final Integer memberUniqueNameField;
	private final Integer memberCaptionField;
	
	/**
	 * Constructor with custom DatasetUri.
	 * @param dsUri
	 * @throws OlapException 
	 */
	public OlapHandler(URL dsUri) throws OlapException  {
		if(dsUri == null){
			throw new InvalidParameterException("dsUri is null");
		}
		this.dsUri = dsUri.toString();	
		// init LinkedDataEngine
		this.lde = OlapEngineFactory.getEngine(dsUri.toString());
		Node dsUriNode = new Resource(dsUri.toString());
		
		//populate Metadata
		Restrictions restrictions = new Restrictions();
		restrictions.cubeNamePattern = dsUriNode;
		this.dimensions = lde.getDimensions(restrictions);
		this.measures = lde.getMeasures(restrictions);
		this.members = lde.getMembers(restrictions);
		this.datasetInformation = lde.getDatasetInformation();
	
		//populate MetadataFields
		dimensionUniqueNameField = OlapHelper.getMetadataMap(dimensions).get("?DIMENSION_UNIQUE_NAME");
		dimensionCaptionField = OlapHelper.getMetadataMap(dimensions).get("?DIMENSION_CAPTION");
		dimensionDescriptionField = OlapHelper.getMetadataMap(dimensions).get("?DESCRIPTION");
		measureUniqueNameField = OlapHelper.getMetadataMap(measures).get("?MEASURE_UNIQUE_NAME");
		measureCaptionField = OlapHelper.getMetadataMap(measures).get("?MEASURE_CAPTION");
		measureIsVisibleField = OlapHelper.getMetadataMap(measures).get("?MEASURE_IS_VISIBLE");
		measureAggregatorField = OlapHelper.getMetadataMap(measures).get("?MEASURE_AGGREGATOR");
		memberUniqueNameField = OlapHelper.getMetadataMap(members).get("?MEMBER_UNIQUE_NAME");
		memberCaptionField = OlapHelper.getMetadataMap(members).get("?MEMBER_CAPTION");
		
		//fill resultHeaderNames
		this.resultHeaderNames = new HashMap<String, String>();		
		for(Node[] dimension: dimensions){
			Node uniqueNameNode = dimension[dimensionUniqueNameField];
			String uniqueName = uniqueNameNode.toString();
			String encodedUniqueName = Olap4ldLinkedDataUtil.makeUriToVariable(uniqueNameNode).toString();
			resultHeaderNames.put(encodedUniqueName, uniqueName);
		}
		for(Node[] measure: measures){
			Node uniqueNameNode = measure[measureUniqueNameField];
			String uniqueName = uniqueNameNode.toString();
			String encodedUniqueName = Olap4ldLinkedDataUtil.makeUriToVariable(uniqueNameNode).toString();
			resultHeaderNames.put(encodedUniqueName, uniqueName);
		}
	}
	
	/**
	 * Get an OlapResult from given Parameters. General Approach:<br>
	 * - show selected measures, show all if none selected<br>
	 * - show selected dimensions, show all if none selected<br>
	 * - filter selected members<br>
	 * @param reqParameterMap Map with keys{'member', 'dimension', 'measure'} (see olap2seo.server.RequestParameter),
	 * mapping to a String[] of MetatdataUniqueNames
	 * @return OlapResult
	 * @throws OlapException in case of failure while executing OlapQuery
	 */
	public OlapResult getOlapResult(final OlapRequest olapRequest) throws OlapException {
		LogicalOlapQueryPlan queryPlan = this.generateLogicalOlapQueryPlan(olapRequest);
		List<Node[]> resultList = this.lde.executeOlapQuery(queryPlan);
		OlapResult result = prepareOlapResult(resultList);
		return result;
	}
	
	public Map<String, String> getLabelMap(){
		Map<String, String> labelMap = new HashMap<String, String>();
		
		for(Node[] dimension: getDimensions()){
			String uniqueName = dimension[dimensionUniqueNameField].toString();
			String caption = dimension[dimensionCaptionField].toString();
			if(!uniqueName.equals("null") && !caption.equals("null")){
				labelMap.put(uniqueName, caption);
			}
		}
		
		for(Node[] measure: getMeasures()){
			String uniqueName = measure[measureUniqueNameField].toString();
			String caption = measure[measureCaptionField].toString();
			if(!uniqueName.equals("null") && !caption.equals("null")){
				labelMap.put(uniqueName, caption);
			}
		}
		
		for(Node[] member: getMembers()){
			String uniqueName = member[memberUniqueNameField].toString();
			String caption = member[memberCaptionField].toString();
			if(!uniqueName.equals("null") && !caption.equals("null")){
				labelMap.put(uniqueName, caption);
			}
		}
		
		return labelMap;
	}
	
	public Model getRdfModel(){
		return lde.getDataset();
	}
	
	public List<Node[]> sparql(String query){
		if(query == null){
			return new ArrayList<Node[]>();
		}
		List<Node[]> result = this.lde.sparql(query, false);
		return result;
	}
		
	public List<Node[]> getDimensions() {
		return this.dimensions;
	}
	
	public List<Node[]> getMeasures() {
		return this.measures;
	}
	
	public List<Node[]> getMembers() {
		return this.members;
	}
	
	public List<Node[]> getDatasetInformation() {
		return this.datasetInformation;
	}
	
	/*#############------------####################
	 * 
	 * Private Methods
	 * 
	 *#############------------####################*/
	
	private OlapResult prepareOlapResult(List<Node[]> resultList){
		if(resultList == null){
			return null;
		}
		
		OlapResult result = new OlapResult();
		
		result.setRows(resultList);
		
		int dimensionCount = 0;
		int measureCount = 0;
		
		//prepare Columns
		resultList = transformResultHeader(resultList);
		Map<String, OlapResultColumn> columns = new HashMap<String, OlapResultColumn>();
		Node[] header = resultList.get(0);
		
		for(int i = 0; i< header.length; i++){

			String uniqueName = header[i].toString();
			OlapResultColumn column = new OlapResultColumn();
			column.setUniqueName(uniqueName);
			
			Node[] dimension = getDimension(uniqueName);
			boolean isDimension = (dimension != null);
			
			Node[] measure = null;
			if(!isDimension){
				measure = getMeasure(uniqueName);
			}
			boolean isMeasure = (measure != null);
			
			if(isDimension){
				dimensionCount ++;
				column.setType(OlapResultColumnType.DIMENSION);
				column.setDescription(dimension[dimensionDescriptionField].toString());
				column.setLabel(dimension[dimensionCaptionField].toString());
				Set<String> memberSet = new HashSet<String>();
				for(int j = 1; j < resultList.size(); j ++){
					Node member = resultList.get(j)[i];
					memberSet.add(member.toString());
				}
				column.setMembers(memberSet);
			}
					
			else if(isMeasure){
				measureCount ++;
				column.setType(OlapResultColumnType.MEASURE);
				column.setLabel(measure[measureCaptionField].toString());
				column.setMeasureAggregator(measure[measureAggregatorField].toString());
				column.setIsVisible( measure[measureIsVisibleField].toString());
			}
			
			columns.put(uniqueName, column);
		}
		result.setColumns(columns);
		
		//fill ResultInformation
		OlapResultInformation resultInfo = new OlapResultInformation();
		resultInfo.setDimensionCount(dimensionCount);
		resultInfo.setMeasureCount(measureCount);
		result.setResultInformation(resultInfo);

		//prepare MemberMap		
		Map<String, OlapResultMember> memberMap = new HashMap<String, OlapResultMember>();
		boolean first = true;
		for(Node[] nodes : resultList){
			if(first){
				first = false;
				continue;
			}
			for(int i=0; i< nodes.length; i++){
				String columnUniqueName = header[i].toString();
				//ignore MeasureRows
				if(columns.get(columnUniqueName).getType() == OlapResultColumnType.MEASURE){
					continue;
				}
				OlapResultMember memberMapEntry = new OlapResultMember();
				memberMapEntry.setDimensionUniqueName(columnUniqueName);
				String memberUniqueName = nodes[i].toString();
				if(!memberUniqueName.equals("null")){
					memberMapEntry.setUniqueName(memberUniqueName);					
					Node[] member = getMember(memberUniqueName);
					if(member != null){
						String caption = member[memberCaptionField].toString();
						if(caption.equals("null")){
							memberMapEntry.setLabel(memberUniqueName);
						}
						else{
							memberMapEntry.setLabel(caption);
						}
					}
					else{
						memberMapEntry.setLabel(memberUniqueName);
					}
				}
				else{
					memberMapEntry.setLabel(memberUniqueName);
				}
				memberMap.put(memberUniqueName, memberMapEntry);
			}
		}
		result.setMemberMap(memberMap);
		
//		//prepare Rows
//		List<Map<String, OlapResultEntry>> rows = new ArrayList<Map<String, OlapResultEntry>>();
//		for(Node[] nodes : resultList){
//			Map<String, OlapResultEntry> row = new HashMap<String, OlapResultEntry>();	
//			
//			for(int i=0; i< nodes.length; i++){
//				String columnUniqueName = header[i].toString();
//				OlapResultEntry entry = new OlapResultEntry();
//				String uniqueName = nodes[i].toString();
//				if(!uniqueName.trim().equals("null") && columns.get(columnUniqueName).getType().equals(OlapResultColumnType.DIMENSION)){
//					entry.setUniqueName(uniqueName);
//					Node[] member = getMember(uniqueName);
//					String caption = member[memberCaptionField].toString();
//					if(caption.equals("null")){
//						entry.setLabel(uniqueName);
//					}
//					else{
//						entry.setLabel(caption);
//					}
//				}
//				else{
//					entry.setLabel(uniqueName);
//				}
//				//TODO
////				entry.setDataType(null);
//				row.put(header[i].toString(), entry);
//			}
//			rows.add(row);
//		}
//		result.setRows(rows);
	
		return result;
	}
	
	private Node[] getDimension(String uniqueName){
		if(uniqueName != null){
			for(Node[] dimension : this.dimensions){
				if(dimension[dimensionUniqueNameField].toString().equals(uniqueName)){
					return dimension;
				}
			}
		}
		return null;
	}
	
	private Node[] getMeasure(String uniqueName){
		if(uniqueName != null){
			for(Node[] measure : this.measures){
				if(measure[measureUniqueNameField].toString().equals(uniqueName)){
					return measure;
				}
			}
		}
		return null;
	}
	
	private Node[] getMember(String uniqueName){
		if(uniqueName != null){
			for(Node[] member : this.members){
				if(member[memberUniqueNameField].toString().equals(uniqueName)){
					return member;
				}
			}
		}
		return null;
	}
	
//	private void init(final String dsUri) throws OlapException{
//		this.lde = CubesEngineFactory.getEngine(dsUri);
//		Node dsUriNode = new Resource(dsUri);
//		populateMetadata(dsUriNode);
//	}
		
//	private void populateMetadata(final Node dsUriNode) throws OlapException{
//		Restrictions restrictions = new Restrictions();
//		restrictions.cubeNamePattern = dsUriNode;
//		this.dimensions = lde.getDimensions(restrictions);
//		this.measures = lde.getMeasures(restrictions);
//		this.members = lde.getMembers(restrictions);
//	
//		//fill resultHeaderNames
//		this.resultHeaderNames = new HashMap<String, String>();
//		int measureUniqueNameField = OlapHelper.getMetadataMap(measures).get("?MEASURE_UNIQUE_NAME");
//		int dimensionUniqueNameField = OlapHelper.getMetadataMap(dimensions).get("?DIMENSION_UNIQUE_NAME");
//		
//		for(Node[] dimension: dimensions){
//			Node uniqueNameNode = dimension[dimensionUniqueNameField];
//			String uniqueName = uniqueNameNode.toString();
//			String encodedUniqueName = Olap4ldLinkedDataUtil.makeUriToVariable(uniqueNameNode).toString();
//			resultHeaderNames.put(encodedUniqueName, uniqueName);
//		}
//		for(Node[] measure: measures){
//			Node uniqueNameNode = measure[measureUniqueNameField];
//			String uniqueName = uniqueNameNode.toString();
//			String encodedUniqueName = Olap4ldLinkedDataUtil.makeUriToVariable(uniqueNameNode).toString();
//			resultHeaderNames.put(encodedUniqueName, uniqueName);
//		}
//	}
	
	/**
	 * Headers are encoded by Olap4Ld. This method decodes the Headers to UniqueNames
	 * @param result
	 * @return
	 */
	private List<Node[]> transformResultHeader(List<Node[]> result){
		if(result == null){
			return new ArrayList<Node[]>();
		}
		Node[] header = result.get(0);
 		for(int i = 0; i< header.length; i++){
 			String currentHeader = header[i].toString();
 			if(currentHeader.endsWith("_new")){
 				currentHeader = currentHeader.substring(0, currentHeader.length() - 4);
 			}
 			if(currentHeader.endsWith("0")){
 				currentHeader = currentHeader.substring(0, currentHeader.length() -1);
 			}
 			String newHeader = this.resultHeaderNames.get(currentHeader);
 			if(newHeader != null){
 				header[i] = new Resource(newHeader);
 			}
		}
 		return result;
	}
	
	/**
	 * Generates a LogicalOlapQueryPlan.<br>
	 * General approach for creating the LogicalOlapQueryPlan:<br>
	 * - Start with BaseCubeOp<br>
	 * - Project selected Measures<br>
	 * - Slice - Keep Dimensions selected in query + all dimensions of selected members - cut off the rest<br>
	 * - Dice - Filter selected members
	 * @param reqParameterMap Map with keys{'member', 'dimension', 'measure'} (see olap2seo.server.RequestParameter),
	 * mapping to a String[] of MetatdataUniqueNames
	 * @return a LogicalOlapQueryPlan
	 */
	private LogicalOlapQueryPlan generateLogicalOlapQueryPlan(final OlapRequest olapRequest){		
		
		//get requestParameter
		List<String> reqMeasures = olapRequest.getMeasures2project();
		List<String> reqDimensions = olapRequest.getDimensions2keep();
		List<String> reqMembers = olapRequest.getMembers2dice();
				
		OlapOperationGenerator generator = new OlapOperationGenerator(this.lde);
		
		//make basecubeOp with dsUri
		LogicalOlapOp basecube = new BaseCubeOp(this.dsUri);
				
		//make projections: select all measures
		LogicalOlapOp projection = new ProjectionOp(basecube, new ArrayList<Node[]>());	
		if (reqMeasures != null){
			projection = generator.selectMeasures(basecube, reqMeasures);
		}
		
		//make slices: select all chosen dimensions + all dimensions of chosen members. Slice the rest
		LogicalOlapOp slice = new SliceOp(projection, new ArrayList<Node[]>());
		List<String> dim2keep = new ArrayList<String>();
		if(reqMembers != null){
			dim2keep.addAll(this.getMemberDimensions(reqMembers));
		}		
		if(reqDimensions != null){
			dim2keep.addAll(reqDimensions);
		}
		if(!dim2keep.isEmpty()){
			slice = generator.selectDimensions(projection, dim2keep);
		}
				
		//make dices: Filter dimensions for selected members. Only one filter per dimension possible
		LogicalOlapOp dice = new SliceOp(slice, new ArrayList<Node[]>());
		if(reqMembers != null){
			dice= generator.selectMembers(slice, reqMembers);
		}
		
		return new LogicalOlapQueryPlan(dice);
	}
	
	/**
	 * Returns a list of uniqueNames of Dimensions corresponding to members
	 * @param members Array of MemberUniqeNames
	 * @return List of DimensionUniqueNames
	 */
	private List<String> getMemberDimensions(List<String> members){
		List<String> dimOfMembers = new ArrayList<String>();
		Map<String, Integer> memberMap = OlapHelper.getMetadataMap(this.members);
		Integer memberUniqueNameField = memberMap.get(OlapOperationGenerator.MEMBER_UNIQUE_NAME);
		Integer dimensionUniqueNameField = memberMap.get(OlapOperationGenerator.DIMENSION_UNIQUE_NAME);
		
		for(String memberUniqueName : members){
			for(Node[] member : this.members){
				if(member[memberUniqueNameField].toString().equals(memberUniqueName)){
					dimOfMembers.add(member[dimensionUniqueNameField].toString());
				}
			}
		}
		return dimOfMembers;
	}
	
}
