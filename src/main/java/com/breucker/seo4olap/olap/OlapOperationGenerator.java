package com.breucker.seo4olap.olap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.olap4j.OlapException;
import org.olap4j.driver.olap4ld.helper.Olap4ldLinkedDataUtil;
import org.olap4j.driver.olap4ld.linkeddata.DiceOp;
import org.olap4j.driver.olap4ld.linkeddata.LinkedDataCubesEngine;
import org.olap4j.driver.olap4ld.linkeddata.LogicalOlapOp;
import org.olap4j.driver.olap4ld.linkeddata.ProjectionOp;
import org.olap4j.driver.olap4ld.linkeddata.Restrictions;
import org.olap4j.driver.olap4ld.linkeddata.RollupOp;
import org.olap4j.driver.olap4ld.linkeddata.SliceOp;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;

/**
 * @author Daniel Breucker
 *
 */
class OlapOperationGenerator {
	
	public static final String MEASURE_UNIQUE_NAME = "?MEASURE_UNIQUE_NAME";
	public static final String DIMENSION_UNIQUE_NAME = "?DIMENSION_UNIQUE_NAME";
	public static final String MEMBER_UNIQUE_NAME = "?MEMBER_UNIQUE_NAME";
	public static final String HIERARCHY_UNIQUE_NAME = "?HIERARCHY_UNIQUE_NAME";
	
	private static final Logger logger = Logger.getLogger(OlapOperationGenerator.class.getName());	
	
	private final LinkedDataCubesEngine lde;
	private List<Node[]> members;
	private List<Node[]> dimensions;
	private List<Node[]> measures;
	
	public OlapOperationGenerator(LinkedDataCubesEngine lde) {
		this.lde = lde;
		populateMetadata();
	}

	/**
	 * Generates a ProjectionOp. Projects all listed measures that match with UniqueNameMapper
	 * @param inputOp a preceding LogicalOlapOp
	 * @param measures2project an Array of uniqueNames of measures
	 * @return ProjectionOp with selected measures
	 */
	public LogicalOlapOp selectMeasures(LogicalOlapOp inputOp, List<String> measures2project){
		if(inputOp == null){
			return null;
		}
		if(measures2project == null || measures2project.isEmpty()){
			return new ProjectionOp(inputOp, new ArrayList<Node[]>());
		}
		
		//select Measures through projection
		List<Node[]> measures = new ArrayList<Node[]>();
		measures.add(0, this.measures.get(0));
		for(String param : measures2project){
			Node[] measure2add = this.getNodes(this.measures, MEASURE_UNIQUE_NAME, param);
			if (measure2add != null){
				measures.add(measure2add);
			}
		}		
		return new ProjectionOp(inputOp, measures);		
	}
	
	
	/**
	 * Slices all Dimensions, that are not selected in dim2keep.
	 * @param inputOp a preceding LogicalOlapOp
	 * @param dim2keep UniqueNames of Dimensions, that are not to be sliced
	 * @return LogicalOlapOp with a slice of all not selected dimensions, 
	 * null if input parameter are unreasonable
	 * 
	 */
	public LogicalOlapOp selectDimensions(LogicalOlapOp inputOp, List<String> dim2keep){
		if(inputOp == null){
			return null;
		}
		if(dim2keep == null || dim2keep.isEmpty()){
			return new SliceOp(inputOp, new ArrayList<Node[]>());
		}
		
		//we want to keep all dim in dim2keep - all others are sliced 		
		List<Node[]> dim2Slice = new ArrayList<Node[]>();
		Map<String, Integer> dimensionMap = OlapHelper.getMetadataMap(this.dimensions);
		Integer dimUniqueNameField = dimensionMap.get(DIMENSION_UNIQUE_NAME);
		
		//check for every Dimension if its uniqueName is in dim2keep 
		boolean first = true;
		for(Node[] dim : this.dimensions){
			String dimUniqueName = dim[dimUniqueNameField].toString();
			
			//exclude measure dimension
			if(dimUniqueName.equals(Olap4ldLinkedDataUtil.MEASURE_DIMENSION_NAME)){
				continue;
			}
			//first Element is header - include only once
			if(first) {
				first = false;
				dim2Slice.add(dim);
				continue;
			}
			
			boolean remove = true;
			for(String dim2keepUniqueName : dim2keep){
				if(dim2keepUniqueName.equals(dimUniqueName)){
					remove = false;
				}
			}
			if(remove){
				dim2Slice.add(dim);
			}
		}
		
		LogicalOlapOp slice = new SliceOp(inputOp, dim2Slice);
		LogicalOlapOp rollup = new RollupOp(slice, new ArrayList<Node[]>(), new ArrayList<Node[]>());
		
		//somehow this is needed - maybe we can put this in method
		Restrictions restrictions = new Restrictions();
		Node dimensionUniqueName = new Resource(dim2Slice.get(0)[dimUniqueNameField].toString());
		restrictions.dimensionUniqueName = dimensionUniqueName;
		List<Node[]> rollupssignature;
		try {
			rollupssignature = lde.getHierarchies(restrictions);
			List<Node[]> rollups = lde.getLevels(restrictions);
			rollup = new RollupOp(slice, rollupssignature, rollups);
		} catch (OlapException e) {
			logger.severe("Failed to do rollups. OlapException:" + e.getMessage().toString());
		}	
		return rollup;
	}
		
	/**
	 * Dices selected Members, i.e. filters one dimension for the member
	 * @param inputOp List of members to dice
	 * @param members2keep List of members to Filter
	 * @return LogicalOlapOp fulfilling the filter
	 */
	public LogicalOlapOp selectMembers(LogicalOlapOp inputOp, List<String> members2keep){
		if(inputOp == null){
			return null;
		}
		if(members2keep == null || members2keep.isEmpty()){
			return new ProjectionOp(inputOp, new ArrayList<Node[]>());
		}
		
		//setup constants
		final Map<String, Integer> memberMap = OlapHelper.getMetadataMap(this.members);
		final Integer memberUniqueNameField = memberMap.get(MEMBER_UNIQUE_NAME);
		final Integer hierarchyUniqueNameField = memberMap.get(HIERARCHY_UNIQUE_NAME);
		final Integer dimensionUniqueNameField = memberMap.get(DIMENSION_UNIQUE_NAME);
		
		//create list of members2dice
		List<Node[]> members2dice = new ArrayList<Node[]>();
		boolean first = true;
		for(Node[] member: this.members){
			if(first) {
				first = false;
				members2dice.add(member);
				continue;
			}
			for(String member2keepUniqueName : members2keep){
				if(member[memberUniqueNameField].toString().equals(member2keepUniqueName)){
					members2dice.add(member);
				}
			}
		}
		
		//generate hierarchySignature
		List<Node[]> hierarchySignature = new ArrayList<Node[]>();
		// First is header
		first = true;
		for (Node[] member : members2dice) {
			if (first) {
				first = false;
				continue;
			}
			Restrictions restrictions = new Restrictions();
			Node dimensionUniqueName = new Resource(member[dimensionUniqueNameField].toString());
			restrictions.dimensionUniqueName = dimensionUniqueName;
			Node hierarchyUniqueName = new Resource(member[hierarchyUniqueNameField].toString());
			restrictions.hierarchyUniqueName = hierarchyUniqueName;
			List<Node[]> hierarchies = this.getHierarchies(restrictions);

			// At start, add header
			if (hierarchySignature.isEmpty()) {
				hierarchySignature.add(hierarchies.get(0));
			}
			hierarchySignature.add(hierarchies.get(1));
		}
		OlapHelper.printMetadata(hierarchySignature, "hierarchysignature");
		
		List<List<Node[]>> aMemberCombinations = new ArrayList<List<Node[]>>();
		aMemberCombinations.add(members2dice);
		LogicalOlapOp dice = new DiceOp(inputOp, hierarchySignature, aMemberCombinations);
				
		return dice;		
	}
		
	/**
	 * Returns a selected Node[] from a List of MetadataItems
	 * @param metadata A MetadataObject, e.g. generated through LinkedDataCubesEngine.getMeasures()
	 * @param parameter The selected parameter in Node[], e.g. "?XX_UNIQUE_NAME"
	 * @param identifier The identifier corresponding to the parameter, e.g. "revenue"
	 * @return The selected Node[], Null if no Node[] was found with given inputs
	 */
	private Node[] getNodes(List<Node[]> metadata, String parameter, String identifier){
		if(metadata != null){
			Map<String, Integer> metadataMap = OlapHelper.getMetadataMap(metadata);
			Integer field = metadataMap.get(parameter);
			if(field != null){
				for (Node[] nodes : metadata){
					if (nodes[field].toString().equals(identifier)){
						return nodes;
					}
				}
			}
		}
		return null;
	}
	
	/*#############------------####################
	 * 
	 * Private Methods
	 * 
	 *#############------------####################*/
	
	private void populateMetadata(){
		Restrictions restrictions = new Restrictions();
		
		try {
			this.dimensions = lde.getDimensions(restrictions);
			this.measures = lde.getMeasures(restrictions);
			this.members = lde.getMembers(restrictions);
			
		} catch (OlapException e) {
			logger.severe("Failed to populateMetadata. OlapException:" + e.getMessage().toString());
		}		
	}
	
	private List<Node[]> getHierarchies(Restrictions restrictions){
		try {			
			return lde.getHierarchies(restrictions);			
		} catch (OlapException e) {
			logger.severe("Failed to getHierarchies. OlapException:" + e.getMessage().toString());
			return new ArrayList<Node[]>();
		}
	}
}
