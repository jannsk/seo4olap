package com.breucker.seo4olap.main;

import java.net.URL;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.yars.nx.Node;

import com.breucker.seo4olap.olap.OlapRequest;
import com.breucker.seo4olap.olap.OlapResult;
import com.breucker.seo4olap.olap.OlapResultColumnType;
import com.breucker.seo4olap.olap.OlapResultInformation;

/**
 * @author dbr
 *
 */
class ResultGenerator {
	
	private final OlapRequest olapRequest;
	private final OlapResult olapResult;
	private final URL dsUri;
	private final ResultTextGenerator textGenerator;
	private final OlapRequestGenerator requestGenerator;
	private final ConfigurationManager configManager;
	private final List<String> dicedMembers;
	private final List<String> projectedMeasures;
	private final List<String> freeDimensions;
	private final Map<String, String> measureMemberMap;
	
	
	public ResultGenerator(OlapRequest olapRequest, OlapResult olapResult) {
		if(olapRequest == null || olapResult == null){
			throw new InvalidParameterException("InputParameter cannot be null");
		}
		this.configManager = ConfigurationManagerFactory.getConfigurationManager();
		this.olapRequest = olapRequest;
		this.olapResult = olapResult;
		this.dsUri = olapRequest.getDatasetUri();
		this.textGenerator = new ResultTextGenerator(olapRequest.copy());
		this.requestGenerator = new OlapRequestGenerator(dsUri);
		this.projectedMeasures = textGenerator.getProjectedMeasures();
		this.dicedMembers = textGenerator.getDicedMembers();
		this.freeDimensions = textGenerator.getFreeDimensions();
		this.measureMemberMap = initMeasureMemberMap();
	}

	public Result getResult(){
		Result result = new Result();
		result.setTable(getTable());
		result.setFilters(textGenerator.getFilters());
		result.setDatasetDescription(textGenerator.getDatasetDescription());
		result.setLicenceLink(getLicenceLink());
		result.setSourceLink(getSourceLink());
		result.setChangeViewLinks(getChangeViewLinks());
		result.setFilterLinks(getFilterLinks());
		result.setOverviewLink(getOverviewLink());
		result.setMetadataDescription(textGenerator.getMetadataDescription());
		result.setMetadataKeywords(textGenerator.getMetadataKeywords());
		result.setMetadataTitle(textGenerator.getMetadataTitle());
		result.setQueryTitle(textGenerator.getQueryTitle());
		ResultDebugInformation debugInformation = new ResultDebugInformation(olapRequest, dicedMembers, 
				projectedMeasures, freeDimensions);
		result.setDebugInformation(debugInformation);
		return result;
	}

	/*#############------------####################
	 * 
	 * Private Methods
	 * 
	 *#############------------####################*/

	private String getLabel(String uniqueName){
		return textGenerator.getLabel(uniqueName);
	}
	
	private Link getSourceLink() {
		return configManager.getDatasetSourceLink(dsUri);
	}
	
	private Link getLicenceLink() {
		return configManager.getDatasetLicenceLink(dsUri);
	}
	
	private Link getOverviewLink() {
		Link link = LinkGenerator.getDatasetLink(dsUri, false);
		return link;
	}
	
	private Map<String, String> initMeasureMemberMap() {
		Map<String, String> measureMemberMap = new HashMap<String, String>();
		
		for(String member: olapRequest.getMembers2dice()){
			String parent = configManager.getParent(dsUri, member);
			if(configManager.isMeasureDimension(dsUri, parent)){
				for(String measure: olapRequest.getMeasures2project()){
					List<String> members = configManager.getMembers(dsUri, measure);
					if(members != null){
						for(String measureMember: members){
							String memberOfMeasureMember = configManager.getMemberOfMeasure(dsUri, measureMember);
							if(member.equals(memberOfMeasureMember)){
								measureMemberMap.put(measure, measureMember);
							}
						}
					}
				}
			}
		}
		return measureMemberMap;
	}
	
	private List<Link> getChangeViewLinks() {
//		final List<String> requestMembers = olapRequest.getMembers2dice();
//		List<Link> linkList = new ArrayList<Link>();
//
//		//remove all dimensions that are already in dimensions2keep
//		List<String> dimensions = configManager.getDimensions(dsUri);
////		for(String requestDimension : olapRequest.getDimensions2keep()){
////			dimensions.remove(requestDimension);
////		}
//		//remove all measureDimensions
//		List<String> measureDimensions = new ArrayList<String>();
//		for(String dimension : dimensions){
//			if(configManager.isMeasureDimension(dsUri, dimension)){
//				measureDimensions.add(dimension);
//			}
//		}
//		//remove all Dimensions with an active diceMember
//		dimensions.removeAll(measureDimensions);
//		for(String member : requestMembers){
//			dimensions.remove(configManager.getParent(dsUri, member));
//		}
//		
////		linkList.addAll(getLinks(olapRequest, dimensions, IdType.DIMENSION));		
//		
//		for(String dimension: dimensions){
//			OlapRequest newRequest = olapRequest.copy();
//			List<String> requestDimensions = newRequest.getDimensions2keep();
//			requestDimensions.removeAll(configManager.getDimensions(dsUri));
//			requestDimensions.add(dimension);
//			Link link = LinkGenerator.getLink(newRequest);
//			linkList.add(link);
//		}
//		
//		return linkList;
		
		
		
		return LinkGenerator.getChangeViewLinks(olapRequest.copy());
	}
	

	private List<Link> getFilterLinks() {
		List<Link> filterLinks = new ArrayList<Link>();
		Set<String> dimensions = new HashSet<String>();
		dimensions.addAll(this.freeDimensions);
		List<String> dimensions2add = new ArrayList<String>();
		for(String diceMember: olapRequest.getMembers2dice()){
			String parent = configManager.getParent(dsUri, diceMember);
			if(parent != null && !configManager.isMeasureDimension(dsUri, parent)){
				dimensions2add.add(parent);
			}
		}
		dimensions.addAll(dimensions2add);
		for(String dimension: dimensions){
			List<String> members = configManager.getMembers(dsUri, dimension);
			if(members != null){
				for(String member: members){
					String label = getLabel(member);
					OlapRequest request = this.olapRequest.copy();
					List<String> newMembers = request.getMembers2dice();
					String member2delete = "XYZ";
					boolean isDicedMember = false;
					for(String newMember: newMembers){
						if(dimension.equals(configManager.getParent(dsUri, newMember)) 
								|| configManager.isSliceMember(dsUri, newMember)){
							member2delete = newMember;
						}
						if(member.equals(newMember)){
							isDicedMember = true;
						}
					}
					if(isDicedMember){
						continue;
					}
					newMembers.remove(member2delete);
					newMembers.add(member);
					OlapRequest req = requestGenerator.generateOlapRequest(request.getMeasures2project(), request.getDimensions2keep(), newMembers);
					Link link = LinkGenerator.getLink(req, false);
					if(link != null){
						link.setText(label);
						filterLinks.add(link);
					}
				}
			}
		}
		return filterLinks;
	}
	
	private String[][] getTable(){
		String table[][];
		int dimensionCount = olapResult.getResultInformation().getDimensionCount();
		int measureCount = olapResult.getResultInformation().getMeasureCount();
		
		if(dimensionCount == 2 && measureCount == 1){
//			table = createTwoDimTable(olapResult);
			table = createDefaultTable();
		}
		else{
//			table = createBasicTable();
			table = createDefaultTable();
		}
		
		
		return table;
	}
	
	private String[][] createDefaultTable(){
		List<Node[]> rows = olapResult.getRows();
		
		int dimensionCount = this.freeDimensions.size();
		int measureCount = this.projectedMeasures.size();
		int columnCount = dimensionCount + measureCount;
		
		Map<Integer, String> columnMap = new HashMap<Integer, String>();
		
		int rowCount = rows.size();
		String[][] table = new String[rowCount][columnCount];
		
		boolean first = true;
		//iterate Rows
		for(int m = 0; m < rowCount; m ++){
			Node[] row = rows.get(m);
			if(first){
				//iterate Columns
				int n = 0;
				for(int i = 0; i < row.length; i ++){
					String columnUniqueName = row[i].toString();
					if(isShownColumn(columnUniqueName)){
						String label = getLabel(columnUniqueName);
						String measureMember = measureMemberMap.get(columnUniqueName);
						if(measureMember != null){
							label = getLabel(measureMember);
						}
						label = toStartWithUpperCase(label);
						table[m][n] = label;
						n++;
					}
					columnMap.put(i, columnUniqueName);
				}
				first = false;
				continue;
			}
			
			//iterate Columns
			int n = 0;
			for(int i = 0; i < row.length; i ++){
				if(isShownColumn(columnMap.get(i))){
					String uniqueName = row[i].toString();
					String label =  getLabel(uniqueName);
					
			        Double value = null;
			        try{
			        	value = Double.parseDouble(label);
			        }
					catch(Exception e){
						//do nothing
					}
			        if(value != null){	
				        DecimalFormat df = new DecimalFormat("#.##");
			        	label = df.format(value);
			        }
			        else{
			        	label = toStartWithUpperCase(label);
			        }
			        
					table[m][n] = label;
					n++;
				}
			}
		}
		return table;
	}
	
	private String toStartWithUpperCase(String input){
		return input.substring(0, 1).toUpperCase() + input.substring(1, input.length());
	}
	
	private boolean isShownColumn(String columnUniqueName){
		if(columnUniqueName == null){
			return false;
		}
		List<String> shownColumns = new ArrayList<String>();
		
		//handle measureMembers
		List<String> projectedMeasures = new ArrayList<String>(this.projectedMeasures);
		List<String> measures2add = new ArrayList<String>();
		List<String> measures2remove = new ArrayList<String>();
		for(String measure: projectedMeasures){
			if(configManager.isMeasureMember(dsUri, measure)){
				measures2add.add(configManager.getParent(dsUri, measure));
				measures2remove.add(measure);
			}
		}
		projectedMeasures.removeAll(measures2remove);
		projectedMeasures.addAll(measures2add);
		
		shownColumns.addAll(projectedMeasures);
		shownColumns.addAll(this.freeDimensions);
		return shownColumns.contains(columnUniqueName);
	}
	
	private String[][] createBasicTable(){
		List<Node[]> rows = olapResult.getRows();
		OlapResultInformation resultInformation = olapResult.getResultInformation();
		int dimensionCount = resultInformation.getDimensionCount();
		int measureCount = resultInformation.getMeasureCount();
		int columnCount = dimensionCount + measureCount;
		int rowCount = rows.size();
		String[][] table = new String[rowCount][columnCount];
		
		boolean first = true;
		//iterate Rows
		for(int m = 0; m < rowCount; m ++){
			Node[] row = rows.get(m);
			if(first){
				//iterate Columns
				for(int n = 0; n < row.length; n ++){
					String columnUniqueName = row[n].toString();
					String label = getLabel(columnUniqueName);
					table[m][n] = label;
				}
				first = false;
				continue;
			}
			
			//iterate Columns
			for(int n = 0; n < row.length; n ++){
				String uniqueName = row[n].toString();
				String label =  getLabel(uniqueName);
				table[m][n] = label;
			}
		}
		return table;
		
		
		
		
		
		
		
//		Map<String, OlapResultColumn> columns = olapResult.getColumns();
//		List<Map<String, OlapResultEntry>> rows = olapResult.getRows();
//		OlapResultInformation resultInformation = olapResult.getResultInformation();
//		int dimensionCount = resultInformation.getDimensionCount();
//		int measureCount = resultInformation.getMeasureCount();
//		int columnCount = dimensionCount + measureCount;
//		int rowCount = resultInformation.getRowCount();
//		String[][] table = new String[rowCount + 1][columnCount];
//		
//		//get Columns
//		String[] dimensions = new String[dimensionCount];
//		String[] measures = new String[measureCount];
//		int dimensionField = 0;
//		int measureField = 0;
//		
//		Iterator<Entry<String, OlapResultColumn>> it = columns.entrySet().iterator();
//		while(it.hasNext()){
//			OlapResultColumn column = it.next().getValue();
//			if(column.getType().equals(OlapResultColumnType.DIMENSION)){
//				dimensions[dimensionField] = column.getUniqueName();
//				dimensionField ++;
//			}
//			if(column.getType().equals(OlapResultColumnType.MEASURE)){
//				measures[measureField] = column.getUniqueName();
//				measureField ++;
//			}
//		}
//		
//		String[] columnUniqueNames = new String[columnCount];
//		for(int i = 0; i< dimensions.length; i++){
//			columnUniqueNames[i] = dimensions[i];
//		}
//		for(int i = 0; i< measures.length; i++){
//			columnUniqueNames[i + dimensionCount] = measures[i];
//		}
//		
//		//populate Columns
//		for(int i = 0; i< columnUniqueNames.length; i++){
//			table[0][i] = columns.get(columnUniqueNames[i]).getLabel();
//		}
//		
//		//populate Rows
//		//iterate rows
//		for(int m = 0; m < rowCount; m++){
//			//iterate columns
//			for(int n = 0; n < columnCount; n++){
//				String columnKey = columns.get(columnUniqueNames[n]).getUniqueName();
//				table[m + 1][n] = rows.get(m).get(columnKey).getLabel();
//			}
//		}
//		return table;
	}
	
	private String[][] createTwoDimTable(OlapResult olapResult){
		return null;
	}
	
	private boolean resultColumnIsDimension(int columnField){
		Map<Integer, String> columnFieldNameMap = getColumnFieldNameMap(olapResult.getRows().get(0));
		String columnName = columnFieldNameMap.get(columnField);
		return olapResult.getColumns().get(columnName).getType() == OlapResultColumnType.DIMENSION;	
	}
	
	private Map<Integer, String> getColumnFieldNameMap(Node[] resultHeader) {
		Map<Integer, String> mapFields = new HashMap<Integer, String>();
		for (int i = 0; i < resultHeader.length; i++) {
			mapFields.put(i, resultHeader[i].toString());
		}
		return mapFields;
	}
	

}
