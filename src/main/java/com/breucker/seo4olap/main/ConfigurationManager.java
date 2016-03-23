package com.breucker.seo4olap.main;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ConfigurationManager {
	
	private final Configuration config;
	private final RuntimeContext context;
	private final Map<URL, DatasetConfiguration> datasetMap;
	private final Map<String, URL> datasetUrisOfId;
	private final Map<String, URL> datasetUrisOfEndpoint;
	private final Map<URL, Map<String, String>> measureMemberUniqueNames;
	private final Map<URL, Map<String, String>>  measureMemberParents;
	private final Map<URL, Map<String, String>>  memberParents;
	private final Map<URL, Map<String, List<String>>> members;
	private final Map<URL, Map<String, DimensionConfiguration>> dimensionMeasures;
	private final Map<URL, Map<String, MetadataConfiguration>> uniqueNameMetadataMap;
	private final Map<URL, Map<String, MetadataConfiguration>> idMetadataMap;
	private final Map<URL, Map<String, MetadataConfiguration>> sliceMembers;
	private final Map<URL, Map<String, MetadataConfiguration>> dimensions;
	private final Map<URL, Map<String, MetadataConfiguration>> measures;
	
	
	/**
	 * Creates a ConfigurationManager, that allows access to all relevant Configuration Parameter.
	 * For Singleton Instance use ConfigurationManagerFactory.getConfigurationManager().
	 */
	ConfigurationManager() {
		this.config = ConfigurationHelper.getConfiguration();
		this.context = getConfigurationContext();
		this.datasetMap = getDatasetMap(config);
		this.datasetUrisOfId = getDatasetUriMap(config, MapType.ID);
		this.datasetUrisOfEndpoint = getDatasetUriMap(config, MapType.ENDPOINT);
		this.measureMemberUniqueNames = getMeasureMemberMap(config, MapType.MEASURE_MEMBER);
		this.measureMemberParents = getMeasureMemberMap(config, MapType.MEASURE_PARENT);
		this.memberParents = getMemberParentsMap(config);
		this.members = getMembers(config);
		this.dimensionMeasures = getDimensionMeasureMap(config);
		this.uniqueNameMetadataMap = getMetadataMap(config, MapType.UNIQUE_NAME);
		this.idMetadataMap = getMetadataMap(config, MapType.ID);
		this.sliceMembers = getMetadataMap(config, MapType.SLICE_MEMBER);
		this.dimensions = getMetadataMap(config, MapType.DIMENSION);
		this.measures = getMetadataMap(config, MapType.MEASURE);
	}
	
	public List<URL> getDatasetUris(){
		List<URL> datasetUris = new ArrayList<URL>();
		Iterator<DatasetConfiguration> it = this.datasetMap.values().iterator();
		while(it.hasNext()){
			String id = it.next().getId();
			if(id != null){
				URL uri = getDatasetUriOfId(id);
				if(uri != null){
					datasetUris.add(uri);
				}
			}
		}
		return datasetUris;	
	}
	
	public RuntimeContext getContext(){
		return this.context;
	}
	
	public URL getDatasetUriOfId(String dsId){
		return datasetUrisOfId.get(dsId);
	}
	
	public URL getDatasetUriOfEndpoint(String endpoint){
		return datasetUrisOfEndpoint.get(endpoint);
	}
	
	public String getBaseUri(){
		return this.config.getBaseUri();
	}
	
	public String getVersion(){
		return this.config.getVersion();
	}
	
	public List<Link> getStaticSites(){
		String baseUri = this.getBaseUri();
		if(baseUri == null){
			baseUri = "";
		}
		List<Link> staticSitesAbsolute = new ArrayList<Link>();
		List<Link> staticSitesRelative = this.config.getStaticSites();
		if(staticSitesRelative == null){
			return staticSitesAbsolute;
		}
		for(Link relativeLink: staticSitesRelative){
			Link link = new Link();
			String relativeUrl = relativeLink.getUrl();
			if(relativeUrl == null){
				continue;
			}
			String url = baseUri + relativeUrl;
			link.setUrl(url);
			String text = relativeLink.getText();
			if(text == null){
				link.setText(url);
			}
			else {
				link.setText(text);
			}
			staticSitesAbsolute.add(link);
		}
		return staticSitesAbsolute;
	}
	
	public boolean isInitOnStartup(URL dsUri){
		DatasetConfiguration dataset = this.datasetMap.get(dsUri);
		if(dataset == null){
			return false;
		}
		return dataset.isInitOnStartup();
	}
	
	public SitemapConfiguration getSitemapConfiguration(URL dsUri){
		DatasetConfiguration dataset = this.datasetMap.get(dsUri);
		if(dataset == null){
			return null;
		}
		SitemapConfiguration sitemap = dataset.getSitemap();
		if(sitemap == null){
			return null;
		}
		return sitemap.clone();
	}
	
	public String getDatasetDescription(URL dsUri){
		DatasetConfiguration dataset = this.datasetMap.get(dsUri);
		if(dataset == null){
			return null;
		}
		return dataset.getDescription();
	}
	
	public String getDatasetEndpoint(URL dsUri){
		DatasetConfiguration dataset = this.datasetMap.get(dsUri);
		if(dataset == null){
			return null;
		}
		SitemapConfiguration sitemap = dataset.getSitemap();
		if(sitemap == null){
			return null;
		}
		return sitemap.getEndpoint();
	}
	
	public String getDatasetId(URL dsUri){
		DatasetConfiguration dataset = this.datasetMap.get(dsUri);
		if(dataset == null){
			return null;
		}
		return dataset.getId();
	}
	
	public String getDatasetTitle(URL dsUri){
		DatasetConfiguration dataset = this.datasetMap.get(dsUri);
		if(dataset == null){
			return null;
		}
		return dataset.getTitle();
	}
	
	public Link getDatasetLicenceLink(URL dsUri){
		DatasetConfiguration dataset = this.datasetMap.get(dsUri);
		if(dataset == null){
			return null;
		}
		return dataset.getLicence();
	}
	
	public Link getDatasetSourceLink(URL dsUri){
		DatasetConfiguration dataset = this.datasetMap.get(dsUri);
		if(dataset == null){
			return null;
		}
		return dataset.getSource();
	}
	
	/**
	 * Get UniqueName of any MetadataObject (Dimension, Measure, Member)
	 * @param dsUri: The Dataset Uri
	 * @param id: (unique) id of MetadataObject
	 * @return UniqueName of MetadataObject, null if not found/existend
	 */
	public String getUniqueName(URL dsUri, String id){
		Map<String, MetadataConfiguration> map = this.idMetadataMap.get(dsUri);
		if(map == null){
			return null;
		}
		MetadataConfiguration metadata = map.get(id);
		if(metadata == null){
			return null;
		}
		return metadata.getUniqueName();
	}
	
	/**
	 * Get Id of any MetadataObject (Dimension, Measure, Member)
	 * @param dsUri: The Dataset Uri
	 * @param uniqueName of MetadataObject
	 * @return Id of MetadataObject, null if not found/existend
	 */
	public String getId(URL dsUri, String uniqueName){
		Map<String, MetadataConfiguration> map = this.uniqueNameMetadataMap.get(dsUri);
		if(map == null){
			return null;
		}
		MetadataConfiguration metadata = map.get(uniqueName);
		if(metadata == null){
			return null;
		}
		return metadata.getId();
	}
	
	/**
	 * Get Label of any MetadataObject (Dimension, Measure, Member)
	 * @param dsUri: The Dataset Uri
	 * @param uniqueName of MetadataObject
	 * @return Label of MetadataObject, null if not found/existend
	 */
	public String getLabel(URL dsUri, String uniqueName){
		Map<String, MetadataConfiguration> map = this.uniqueNameMetadataMap.get(dsUri);
		if(map == null){
			return null;
		}
		MetadataConfiguration metadata = map.get(uniqueName);
		if(metadata == null){
			return null;
		}
		return metadata.getLabel();
	}
	
	/**
	 * Get a Map<uniqueName, label> of all existing labels in datasets
	 * @param dsUri: The Dataset Uri
	 * @return Map with key: uniqueName, value: label, null if dataset not in configuration
	 */
	public Map<String, String> getLabelMap(URL dsUri){
		Map<String, MetadataConfiguration> map = this.uniqueNameMetadataMap.get(dsUri);
		if(map == null){
			return null;
		}
		Map<String, String> labelMap = new HashMap<String, String>();
		Iterator<MetadataConfiguration> it = map.values().iterator();
		while(it.hasNext()){
			MetadataConfiguration metadata = it.next();
			String uniqueName = metadata.getUniqueName();
			String label = metadata.getLabel();
			if(uniqueName != null && label != null){
				labelMap.put(uniqueName, label);
			}
		}
		return labelMap;
	}
	
	/**
	 * Get Members of dimension or measure (if measure has measureMembers)
	 * @param dsUri: The DatasetUri
	 * @param uniqueName of either dimension or measure
	 * @return a List of Members (corresponding uniqueName)
	 */
	public List<String> getMembers(URL dsUri, String uniqueName){
		Map<String, List<String>> map = this.members.get(dsUri);
		if(map == null){
			return null;
		}
		List<String> list = map.get(uniqueName);
		if(list == null){
			return null;
		}
		return new ArrayList<String>(list);
	}
	
	/**
	 * Get the UniqueName of a Member corresponding to a Measure.
	 * @param dsUri: The DatasetUri
	 * @param measureUniqueName 
	 * @return uniqueName of member, null if measure does not have a member
	 */
	public String getMemberOfMeasure(URL dsUri, String measureUniqueName){
		Map<String, String> map = this.measureMemberUniqueNames.get(dsUri);
		if(map == null){
			return null;
		}
		return map.get(measureUniqueName);
	}
	
	/**
	 * Check if a UniqueName corresponds to a MeasureMember
	 * @param dsUri: The DatasetUri
	 * @param uniqueName of Measure
	 * @return
	 */
	public boolean isMeasureMember(URL dsUri, String uniqueName){
		Map<String, MetadataConfiguration> map = this.uniqueNameMetadataMap.get(dsUri);
		if(map == null){
			return false;
		}
		return (map.get(uniqueName) instanceof MeasureMemberConfiguration);
	}
	
	/**
	 * Check if a Dimension is a MeasureDimension
	 * @param dsUri: The DatasetUri
	 * @param dimensionUniqueName
	 * @return
	 */
	public boolean isMeasureDimension(URL dsUri, String dimensionUniqueName){
		if(dimensionUniqueName == null){
			return false;
		}
		Map<String, DimensionConfiguration> map = this.dimensionMeasures.get(dsUri);
		if(map == null){
			return false;
		}
		return map.get(dimensionUniqueName) != null;
	}
	
	/**
	 * Get the sliceMember of a Dimension
	 * @param dsUri: The DatasetUri
	 * @param dimensionUniqueName UniqueName of Dimension
	 * @return UniqueName of SliceMember, null if not existend/found
	 */
	public String getSliceMember(URL dsUri, String dimensionUniqueName){
		Map<String, MetadataConfiguration> map = this.sliceMembers.get(dsUri);
		if(map == null){
			return null;
		}
		MetadataConfiguration sliceMember = map.get(dimensionUniqueName);
		if(sliceMember == null){
			return null;
		}
		return sliceMember.getUniqueName();
	}
	
	/**
	 * Get the sliceLabel of a Dimension
	 * @param dsUri: The DatasetUri
	 * @param dimensionUniqueName UniqueName of Dimension
	 * @return Slicelabel of Dimension, null if not existend/found
	 */
	public String getSliceLabel(URL dsUri, String dimensionUniqueName){
		Map<String, MetadataConfiguration> map = this.dimensions.get(dsUri);
		if(map == null){
			return null;
		}
		MetadataConfiguration dimensionTemp = map.get(dimensionUniqueName);
		if(dimensionTemp instanceof DimensionConfiguration){
			DimensionConfiguration dimension = (DimensionConfiguration) dimensionTemp;
			return dimension.getSliceLabel();
		}
		return null;
	}
	
	/**
	 * Get all Dimensions of a Dataset
	 * @param dsUri The datasetUri
	 * @return a List of UniqueNames of all Dimensions, null if dataset not found or dataset has no dimensions
	 */
	public List<String> getDimensions(URL dsUri){
		Map<String, MetadataConfiguration> map = this.dimensions.get(dsUri);
		if(map == null){
			return null;
		}
		List<String> list = new ArrayList<String>();
		Iterator<MetadataConfiguration> it = map.values().iterator();
		while(it.hasNext()){
			String dimensionUniqueName = it.next().getUniqueName();
			if(dimensionUniqueName != null){
				list.add(dimensionUniqueName);
			}
		}
		return list;
	}
	
	/**
	 * Get all Measures of a Dataset
	 * @param dsUri The datasetUri
	 * @return a List of UniqueNames of all Measures, null if dataset not found or dataset has no dimensions
	 */
	public List<String> getMeasures(URL dsUri){
		Map<String, MetadataConfiguration> map = this.measures.get(dsUri);
		if(map == null){
			return null;
		}
		List<String> list = new ArrayList<String>();
		Iterator<MetadataConfiguration> it = map.values().iterator();
		while(it.hasNext()){
			String measureUniqueName = it.next().getUniqueName();
			if(measureUniqueName != null){
				list.add(measureUniqueName);
			}
		}
		return list;
	}
	
	/**
	 * Checks if a member is a SliceMember of a Dimension
	 * @param dsUri Uri of Dataset
	 * @param memberUniqueName UniqueName of Member
	 * @return true if member is a SliceMember, else false
	 */
	public boolean isSliceMember(URL dsUri, String memberUniqueName){
		Map<String, MetadataConfiguration> map = this.sliceMembers.get(dsUri);
		if(map == null){
			return false;
		}
		Iterator<Entry<String, MetadataConfiguration>> it = map.entrySet().iterator();
		while(it.hasNext()){
			MetadataConfiguration sliceMember = it.next().getValue();
			String sliceMemberUniqueName = sliceMember.getUniqueName();
			if(sliceMemberUniqueName != null && sliceMemberUniqueName.equals(memberUniqueName)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the parent of a member
	 * @param dsUri Uri of Dataset
	 * @param memberUniqueName UniqueName of member (DimensionMember or MeasureMember)
	 * @return UniqueName of Parent (Dimension or Measure), null if not existend/found
	 */
	public String getParent(URL dsUri, String memberUniqueName){
		Map<String, String> map = this.memberParents.get(dsUri);
		if(map == null){
			return null;
		}
		return map.get(memberUniqueName);
	}
	
	/**
	 * Get the parent of a MeasureMember. If a Measure has measureMembers, you get the measure of the corresponding measureMember
	 * @param dsUri Uri of Dataset
	 * @param measureMemberUniqueName UniqueName of measureMember
	 * @return UniqueName of Measure (the parent), null if not existend/found
	 */
	public String getParentMeasureOfMeasureMember(URL dsUri, String measureMemberUniqueName){
		Map<String, String> map = this.measureMemberParents.get(dsUri);
		if(map == null){
			return null;
		}
		return map.get(measureMemberUniqueName);
	}
	
	/*#############------------####################
	 * 
	 * Private Methods
	 * 
	 *#############------------####################*/
	
	private RuntimeContext getConfigurationContext() {
		RuntimeContext context = new RuntimeContext();
		context.setDebugMode(config.isDebugMode());
		return context;
	}
	
	private Map<URL, Map<String, MetadataConfiguration>> getMetadataMap(Configuration configuration, MapType type) {
		List<DatasetConfiguration> datasets = configuration.getDatasets();
		Map<URL, Map<String, MetadataConfiguration>> map = new HashMap<URL, Map<String, MetadataConfiguration>>();
		for(DatasetConfiguration dataset : datasets){
			URL datasetUri = null;
			try {
				datasetUri = new URL(dataset.getUri());
			} catch (MalformedURLException e) {
				continue;
			}
			Map<String, MetadataConfiguration> metadataMap = new HashMap<String, MetadataConfiguration>();
			
			if(type == MapType.DIMENSION){
				List<? extends MetadataConfiguration> dimensions = dataset.getDimensions();			
				if(dimensions != null){	
					putInMap(metadataMap, dimensions, MapType.UNIQUE_NAME);
				}
				map.put(datasetUri, metadataMap);
				continue;
			}
			
			if(type == MapType.MEASURE){
				List<? extends MetadataConfiguration> measures = dataset.getMeasures();			
				if(measures != null){	
					putInMap(metadataMap, measures, MapType.UNIQUE_NAME);
				}
				map.put(datasetUri, metadataMap);
				continue;
			}
			
			List<? extends MetadataConfiguration> dimensions = dataset.getDimensions();			
			if(dimensions != null){	
				if(type == MapType.SLICE_MEMBER){
					for(MetadataConfiguration dimension : dimensions){
						if(dimension instanceof DimensionConfiguration){
							DimensionConfiguration dimensionNew = (DimensionConfiguration)dimension;
							String dimensionUniqueName = dimensionNew.getUniqueName();
							String sliceMemberUniqueName = dimensionNew.getSliceMemberUniqueName();
							List<MetadataConfiguration> members = dimensionNew.getMembers();
							if(members == null || sliceMemberUniqueName == null || dimensionUniqueName == null){
								continue;
							}
							for(MetadataConfiguration member : members){
								String memberUniqueName = member.getUniqueName();
								if(memberUniqueName != null && memberUniqueName.equals(sliceMemberUniqueName)){
									metadataMap.put(dimensionUniqueName, member);
								}
							}
						}
					}
				}
				else{
					putInMap(metadataMap, dimensions, type);
					for(MetadataConfiguration dimension : dimensions){
						if(dimension instanceof DimensionConfiguration){
							DimensionConfiguration dimensionNew = (DimensionConfiguration)dimension;
							putInMap(metadataMap, dimensionNew.getMembers(), type);
						}
					}
				}
			}
			
			//get DimensionMeasures and DimensionMeasureMembers
			List<? extends MetadataConfiguration> dimensionMeasures = dataset.getDimensionMeasures();
			if(dimensionMeasures != null){
				putInMap(metadataMap, dimensionMeasures, type);
				for(MetadataConfiguration dimension : dimensionMeasures){
					if(dimension instanceof DimensionConfiguration){
						DimensionConfiguration dimensionNew = (DimensionConfiguration)dimension;
						putInMap(metadataMap, dimensionNew.getMembers(), type);
					}
				}
			}
			
			//get measures and MeasureMembers
			List<? extends MetadataConfiguration> measures = dataset.getMeasures();
			if(measures != null){
				putInMap(metadataMap, measures, type);
				for(MetadataConfiguration measure : measures){
					if(measure instanceof MeasureConfiguration){
						MeasureConfiguration measureNew = (MeasureConfiguration)measure;
						putInMap(metadataMap, measureNew.getMeasureMembers(), type);
					}
				}
			}
			map.put(datasetUri, metadataMap);
		}
		return map;
	}
	
	
	private Map<URL, DatasetConfiguration> getDatasetMap(Configuration configuration){
		List<DatasetConfiguration> datasetList = configuration.getDatasets();
		Map<URL, DatasetConfiguration> datasetMap = new HashMap<URL, DatasetConfiguration>();
		for(DatasetConfiguration dataset : datasetList){
			URL datasetUri = null;
			try {
				datasetUri = new URL(dataset.getUri());
			} catch (MalformedURLException e) {
				continue;
			}
			datasetMap.put(datasetUri, dataset);
		}
		return datasetMap;
	}
	
	private Map<URL, Map<String, List<String>>> getMembers(Configuration configuration) {
		List<DatasetConfiguration> datasetList = configuration.getDatasets();
		Map<URL, Map<String, List<String>>> memberMapPerDataset = new HashMap<URL, Map<String, List<String>>>();
		for(DatasetConfiguration dataset : datasetList){
			URL datasetUri = null;
			try {
				datasetUri = new URL(dataset.getUri());
			} catch (MalformedURLException e) {
				continue;
			}
			Map<String, List<String>> memberMap = new HashMap<String, List<String>>();
			List<MeasureConfiguration> measures = dataset.getMeasures();
			if(measures != null){
				for(MeasureConfiguration measure : measures){
					List<String> measureMemberList = new ArrayList<String>();
					List<MeasureMemberConfiguration> measureMembers = measure.getMeasureMembers();
					if(measureMembers == null){
						continue;
					}
					for(MeasureMemberConfiguration measureMember : measureMembers){
						String measureMemberUniqueName = measureMember.getUniqueName();
						if(measureMemberUniqueName != null){
							measureMemberList.add(measureMemberUniqueName);
						}
					}
					memberMap.put(measure.getUniqueName(), measureMemberList);
				}
			}
			
			List<DimensionConfiguration> dimensions = dataset.getDimensions();
			List<DimensionConfiguration> dimensionMeasures = dataset.getDimensionMeasures();
			if(dimensions != null){
				if(dimensionMeasures != null){
					dimensions.addAll(dimensionMeasures);
				}
				for(DimensionConfiguration dimension : dimensions){
					List<String> dimensionMemberList = new ArrayList<String>();
					List<MetadataConfiguration> dimensionMembers = dimension.getMembers();
					if(dimensionMembers == null){
						continue;
					}
					for(MetadataConfiguration dimensionMember : dimensionMembers){
						String dimensionMemberUniqueName = dimensionMember.getUniqueName();
						if(dimensionMemberUniqueName != null){
							dimensionMemberList.add(dimensionMemberUniqueName);
						}
					}
					memberMap.put(dimension.getUniqueName(), dimensionMemberList);
				}
			}
			
			memberMapPerDataset.put(datasetUri, memberMap);
		}
		return memberMapPerDataset;
	}
	
	private Map<URL, Map<String, DimensionConfiguration>> getDimensionMeasureMap(Configuration configuration){
		List<DatasetConfiguration> datasets = configuration.getDatasets();
		Map<URL, Map<String, DimensionConfiguration>> map = new HashMap<URL, Map<String, DimensionConfiguration>>();
		for(DatasetConfiguration dataset : datasets){
			URL datasetUri = null;
			try {
				datasetUri = new URL(dataset.getUri());
			} catch (MalformedURLException e) {
				continue;
			}
			Map<String, DimensionConfiguration> dimMeasureMap = new HashMap<String, DimensionConfiguration>();
			List<DimensionConfiguration> dimensionMeasures = dataset.getDimensionMeasures();
			if(dimensionMeasures == null){
				continue;
			}
			for(DimensionConfiguration dimensionMeasure : dimensionMeasures){
				dimMeasureMap.put(dimensionMeasure.getUniqueName(), dimensionMeasure);
			}
			map.put(datasetUri, dimMeasureMap);
		}
		return map;
	}
	
	private  Map<URL, Map<String, String>> getMeasureMemberMap(Configuration configuration, MapType type){
		List<DatasetConfiguration> datasets = configuration.getDatasets();
		Map<URL, Map<String, String>> map = new HashMap<URL, Map<String, String>>();
		for(DatasetConfiguration dataset : datasets){
			URL datasetUri = null;
			try {
				datasetUri = new URL(dataset.getUri());
			} catch (MalformedURLException e) {
				continue;
			}
			
			Map<String, String> measureMemberMap = new HashMap<String, String>();
			List<MeasureConfiguration> measures = dataset.getMeasures();
			if(measures != null){
				for(MeasureConfiguration measure : measures){
					List<MeasureMemberConfiguration> measureMembers = measure.getMeasureMembers();
					if(measureMembers == null){
						continue;
					}
					String parentUniqueName = measure.getUniqueName();
					for(MeasureMemberConfiguration measureMember : measureMembers){
						String measureMemberUniqueName = measureMember.getMeasureMemberUniqueName();
						String measureUniqueName = measureMember.getUniqueName();
						if(type == MapType.MEASURE_MEMBER){
							if(measureMemberUniqueName != null && measureUniqueName != null){
								measureMemberMap.put(measureUniqueName, measureMemberUniqueName);
							}
						}
						if(type == MapType.MEASURE_PARENT){
							if(parentUniqueName != null && measureUniqueName != null){
								measureMemberMap.put(measureUniqueName, parentUniqueName);
							}
						}
						
					}
				}
			}
			map.put(datasetUri, measureMemberMap);
		}
		return map;
	}
	


	private Map<URL, Map<String, String>> getMemberParentsMap(Configuration configuration) {
		List<DatasetConfiguration> datasets = configuration.getDatasets();
		Map<URL, Map<String, String>> map = new HashMap<URL, Map<String, String>>();
		for(DatasetConfiguration dataset : datasets){
			URL datasetUri = null;
			try {
				datasetUri = new URL(dataset.getUri());
			} catch (MalformedURLException e) {
				continue;
			}
			
			//crawl Measures
			Map<String, String> memberMap = new HashMap<String, String>();
			List<MeasureConfiguration> measures = dataset.getMeasures();
			if(measures != null){
				for(MeasureConfiguration measure : measures){
					List<MeasureMemberConfiguration> measureMembers = measure.getMeasureMembers();
					if(measureMembers == null){
						continue;
					}
					String parentUniqueName = measure.getUniqueName();
					for(MeasureMemberConfiguration measureMember : measureMembers){
						String measureUniqueName = measureMember.getUniqueName();
						if(parentUniqueName != null && measureUniqueName != null){
							memberMap.put(measureUniqueName, parentUniqueName);
						}
					}
				}
			}
			
			//crawl dimensions
			List<DimensionConfiguration> dimensionMeasures = dataset.getDimensionMeasures();
			List<DimensionConfiguration> dimensions = dataset.getDimensions();
			
			if(dimensions != null){
				if(dimensionMeasures != null){
					dimensions.addAll(dimensionMeasures);
				}
				for(DimensionConfiguration dimension : dimensions){
					List<MetadataConfiguration> members = dimension.getMembers();
					if(members == null){
						continue;
					}
					String parentUniqueName = dimension.getUniqueName();
					for(MetadataConfiguration member : members){
						String memberUniqueName = member.getUniqueName();
						if(parentUniqueName != null && memberUniqueName != null){
							memberMap.put(memberUniqueName, parentUniqueName);
						}
					}
				}
			}
			map.put(datasetUri, memberMap);
		}
		return map;
	}
	
	
	private void putInMap(Map<String, MetadataConfiguration> map, List<? extends MetadataConfiguration> metadata, MapType type){
		if(metadata == null){
			return;
		}
		for(MetadataConfiguration data : metadata){
			if(type == MapType.UNIQUE_NAME){
				String uniqueName = data.getUniqueName();
				if(uniqueName != null){
					map.put(uniqueName, data);
				}
			}
			if(type == MapType.ID){
				String id = data.getId();
				if(id != null ){
					map.put(id, data);
				}
			}
		}
	}
	
	
	private Map<String, URL> getDatasetUriMap(Configuration configuration, MapType type){
		List<DatasetConfiguration> datasets = configuration.getDatasets();
		Map<String, URL> datasetUris = new HashMap<String, URL>();
		for(DatasetConfiguration dataset : datasets){
			String dsUri = dataset.getUri();
			URL datasetUri = null;
			try {
				datasetUri = new URL(dsUri);
			} catch (MalformedURLException e) {
				continue;
			}
			String key = null;
			if(type == MapType.ID){
				key = dataset.getId();
			}
			if(type == MapType.ENDPOINT){
				SitemapConfiguration sitemap = dataset.getSitemap();
				if(sitemap == null){
					continue;
				}
				key = sitemap.getEndpoint();
			}
			if(key != null){
				datasetUris.put(key, datasetUri);
			}
		}
		return datasetUris;
	}

	private enum MapType{
		UNIQUE_NAME, ID, LABEL, ENDPOINT, SLICE_MEMBER, MEASURE_MEMBER, DIMENSION, MEASURE, MEASURE_PARENT
	}
}
