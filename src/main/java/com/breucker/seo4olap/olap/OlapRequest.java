package com.breucker.seo4olap.olap;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * An OlapRequest will be evaluated by e.g. OlapHandler.
 * Populate it with String[] of Unique-Names.
 * 
 * @author Daniel Breucker
 *
 */
public class OlapRequest {

	private URL datasetUri;
	private List<String> members2dice;
	private List<String> dimensions2keep;
	private List<String> measures2project;
	
	public OlapRequest() {}
	
	/**
	 * An OlapRequest will be evaluated by e.g. OlapHandler.
	 * 
	 * @param members2dice UniqueNames (URI) of Members, that should be diced.
	 * @param dimensions2keep UniqueNames (URI) of Dimensions, that should be kept. All others dimensions are sliced
	 * @param measures2project UniqueNames (URI) of Measures, that should be projected.
	 * @param datasetUri The DatasetUri
	 */
	public OlapRequest(URL datasetUri, List<String> members2dice, List<String> dimensions2keep, List<String> measures2project) {
		if(datasetUri == null){
			throw new InvalidParameterException("datasetUri cannot be null");
		}
		this.datasetUri = datasetUri;
		if(members2dice == null){
			members2dice = new ArrayList<String>();
		}
		this.members2dice = members2dice;
		if(dimensions2keep == null){
			dimensions2keep = new ArrayList<String>();
		}
		this.dimensions2keep = dimensions2keep;
		if(measures2project == null){
			measures2project = new ArrayList<String>();
		}
		this.measures2project = measures2project;
	}

	public List<String> getMembers2dice() {
		return members2dice;
	}

	public List<String> getDimensions2keep() {
		return dimensions2keep;
	}

	public List<String> getMeasures2project() {
		return measures2project;
	}
	
	public URL getDatasetUri() {
		return datasetUri;
	}

	@Override
	public int hashCode(){
		int h = 0;
		for(String item : members2dice){
			h += "members".hashCode();
    		h += (item + item).hashCode();
    	}
		for(String item : dimensions2keep){
			h += "dimensions".hashCode();
    		h += (item + item).hashCode();
    	}
		for(String item : measures2project){
			h += "measures".hashCode();
    		h += (item + item).hashCode();
    	}
		h += datasetUri.hashCode();
        return h;
	}
	
	@Override
	public boolean equals(Object olapRequest){
		if(olapRequest == null){
			return false;
		}
		return this.hashCode() == olapRequest.hashCode();
	}
	
	@Override
	public String toString(){
		return "datasetUri: " + datasetUri.toString() 
		+ ", members2dice: " + members2dice 
		+ ", measure2project: " + measures2project 
		+ ", dimension2keep: " + dimensions2keep + ",";
	}
	
	public OlapRequest copy(){
		URL dsUri = null;
		try {
			dsUri = new URL(this.datasetUri.toString());
		} catch (MalformedURLException e) {
		}
		return new OlapRequest(dsUri, new ArrayList<String>(members2dice), 
				new ArrayList<String>(dimensions2keep), new ArrayList<String>(measures2project));
	}
	
}
