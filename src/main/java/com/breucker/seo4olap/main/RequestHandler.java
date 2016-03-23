package com.breucker.seo4olap.main;

import java.lang.reflect.Type;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.olap4j.OlapException;
import org.openrdf.model.Model;
import org.semanticweb.yars.nx.Node;

import com.breucker.seo4olap.olap.OlapHandler;
import com.breucker.seo4olap.olap.OlapRequest;
import com.breucker.seo4olap.olap.OlapResult;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.gson.reflect.TypeToken;

/**
 * RequestHandler can be used for Requests towards OlapHandler.
 * Answers are persisted by PersistenceManager for fast retrieval.
 * @author Daniel Breucker
 *
 */
public class RequestHandler {

	private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());	
	private final PersistenceManager persistenceManager = new PersistenceManager();
	private final ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
	
	public RequestHandler() {}

	/**
	 * Returns an OlapResult of an OLAP-Request
	 * @param olapRequest
	 * @param recompute if true, result will be recomputed and not retrieved from cache or datastore
	 * @return an OlapResult 
	 * @throws OlapException  
	 */	
	public Result getOlapResult(final OlapRequest olapRequest, final boolean recompute) 
			throws OlapException {
		
		if(olapRequest == null){
			throw new InvalidParameterException("olapRequest cannot be null");
		}
		int key = olapRequest.hashCode();
		
		String kind = "OlapResult-";
		if(olapRequest.getDatasetUri() != null){
			kind += olapRequest.getDatasetUri().toString();
		}
		if(!recompute){
			try {
				Type resultType = new TypeToken<Result>(){}.getType();
				return (Result) persistenceManager.get(key, resultType, kind);
				
			} catch (EntityNotFoundException e1) {
				// compute new result
			}
		}
		try {
			OlapHandler olapHandler = new OlapHandler(olapRequest.getDatasetUri());				
			OlapResult olapResult = olapHandler.getOlapResult(olapRequest);
			olapHandler = null;
			
			ResultGenerator generator = new ResultGenerator(olapRequest, olapResult);
			Result result = generator.getResult();
			
			persistenceManager.put(key, result, kind);
			
			return result;
			
		} catch (OlapException e) {
			logger.warning("failed getOlapResult. OlapException. ");
			throw new OlapException(e);
		}
		
	}
	
	/**
	 * Get a List of all possible Requests for the given datasetUri
	 * @param datasetUri
	 * @param recompute If true the List will be recomputed and not retrieved from Datastore
	 * @return List of RequestUris
	 */
	@SuppressWarnings("unchecked")
	public List<String> getUrlRequestList(final URL datasetUri, final boolean recompute, final Boolean absolutePath) {	
		if(datasetUri == null){
			throw new InvalidParameterException("datasetUri cannot be null");
		}
		String kind = "UrlRequestList";
		int key = datasetUri.hashCode() + kind.hashCode() + absolutePath.hashCode();
		List<String> requests = new ArrayList<String>();
		if(!recompute){
			try {
				Type type = new TypeToken<List<String>>(){}.getType();
				requests = (List<String>) persistenceManager.get(key, type, kind);
				return requests;
				
			} catch (EntityNotFoundException e1) {
				// compute new result
			}
		}
		RequestListGenerator generator = new RequestListGenerator(datasetUri);
		requests = generator.getURLRequestList(absolutePath);
		
		persistenceManager.put(key, requests, kind);
		
		return requests;
		
	}
	
	/**
	 * Get a IndexBean filled with all Information to display 
	 * @param datasetUri
	 * @param recompute If true the IndexBean will be recomputed and not retrieved from Datastore
	 * @return The IndexBean for the Configuration
	 */
	public IndexBean getIndexBean(final boolean recompute) {	
		String kind = "IndexBean";
		int key = kind.hashCode();
		IndexBean indexBean = new IndexBean();
		if(!recompute){
			try {
				Type type = new TypeToken<IndexBean>(){}.getType();
				indexBean = (IndexBean) persistenceManager.get(key, type, kind);
				return indexBean;
				
			} catch (EntityNotFoundException e1) {
				// compute new DatasetBean
			}
		}
		
		List<DatasetBean> datasets = new ArrayList<DatasetBean>();
		for(URL datasetUri: configManager.getDatasetUris()){
			datasets.add(this.getDatasetBean(datasetUri, recompute));
		}
		indexBean.setDatasets(datasets);
		
		persistenceManager.put(key, indexBean, kind);
		
		return indexBean;
	}
	
	/**
	 * Get a DatasetBean filled with all Information to display 
	 * @param datasetUri
	 * @param recompute If true the DatasetBean will be recomputed and not retrieved from Datastore
	 * @return The DatasetBean for the Dataset
	 */
	public DatasetBean getDatasetBean(final URL datasetUri, final boolean recompute) {	
		if(datasetUri == null){
			throw new InvalidParameterException("datasetUri cannot be null");
		}
		String kind = "DatasetBean";
		int key = datasetUri.hashCode() + kind.hashCode();
		DatasetBean dsBean = new DatasetBean();
		if(!recompute){
			try {
				Type type = new TypeToken<DatasetBean>(){}.getType();
				dsBean = (DatasetBean) persistenceManager.get(key, type, kind);
				return dsBean;
				
			} catch (EntityNotFoundException e1) {
				// compute new DatasetBean
			}
		}
		
		dsBean.setTitle(configManager.getDatasetTitle(datasetUri));
		dsBean.setDescription(configManager.getDatasetDescription(datasetUri));
		dsBean.setLicence(configManager.getDatasetLicenceLink(datasetUri));
		dsBean.setSource(configManager.getDatasetSourceLink(datasetUri));
		dsBean.setLink(LinkGenerator.getDatasetLink(datasetUri, false));
		
		SitemapGenerator generator = new SitemapGenerator(datasetUri);
		List<Link> links = generator.getSitemapLinks(false);
		int size = links.size();
		if(size > 21){
			size = 21;
		}
		dsBean.setLinks(links.subList(1, size));
		
		persistenceManager.put(key, dsBean, kind);
		
		return dsBean;
	}
	
	/**
	 * Get the RuntimeContext
	 * @param recompute If true the RuntimeContext will be recomputed and not retrieved from Datastore
	 * @return
	 */
	public RuntimeContext getRuntimeContext(final boolean recompute) {	
		String kind = "RuntimeContext";
		int key = kind.hashCode();
		RuntimeContext context = null;
		if(!recompute){
			try {
				Type type = new TypeToken<RuntimeContext>(){}.getType();
				context = (RuntimeContext) persistenceManager.get(key, type, kind);
				return context;
				
			} catch (EntityNotFoundException e1) {
				// compute new DatasetBean
			}
		}
		
		context = RuntimeContextManager.getContext();
		
		persistenceManager.put(key, context, kind);
		
		return context;
	}
	
	/**
	 * Set the RuntimeContext
	 * @param context the RuntimeContext
	 */
	public void setRuntimeContext(RuntimeContext context) {	
		String kind = "RuntimeContext";
		int key = kind.hashCode();
		
		persistenceManager.put(key, context, kind);
	}
	
	public String getMetadata(final URL datasetUri) throws OlapException{
		
		OlapHandler olapHandler = new OlapHandler(datasetUri);
		String dimensions = PresentationHelper.generateMetadata(olapHandler.getDimensions());
		String measures = PresentationHelper.generateMetadata(olapHandler.getMeasures());
		String members = PresentationHelper.generateMetadata(olapHandler.getMembers());
		String datsetInformation = PresentationHelper.generateMetadata(olapHandler.getDatasetInformation());
		
		String resultHtml = "";
		resultHtml += "<div>";
		resultHtml += "<h4>Dataset Information</h4>";
		resultHtml += datsetInformation;
		resultHtml += "</div>";
		
		resultHtml += "<div>";
		resultHtml += "<h4>Dimensions</h4>";
		resultHtml += dimensions;
		resultHtml += "</div>";
		
		resultHtml += "<div>";
		resultHtml += "<h4>Measures</h4>";
		resultHtml += measures;
		resultHtml += "</div>";
		
		resultHtml += "<div>";
		resultHtml += "<h4>Members</h4>";
		resultHtml += members;
		resultHtml += "</div>";
		return resultHtml;		
	}
	
	/**
	 * Returns an HTML-Representation of an Sparql-Request-Answer
	 * @param dsUri DatasetUri; null returns DefaultDataSEt
	 * @param query a Sparql-Query
	 * @return a String in HTML 
	 */
	public String getSparqlResult(final URL datasetUri, final String query) {		
		try {
			OlapHandler olapHandler;
			olapHandler = new OlapHandler(datasetUri);
			List<Node[]> result = olapHandler.sparql(query);
			
			String output = PresentationHelper.generateHtmlTable(result);
			olapHandler = null;
			return output;
		} catch (OlapException e) {
			//TODO exception lieber werfen
			logger.warning("failed getSparqlResult. OlapException. ");
			e.printStackTrace();
			return PresentationHelper.generateErrorMessage();
		} 
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getLabelMap(final URL datasetUri, boolean recompute) throws OlapException{

		String kind = "LabelMap";
		int key = datasetUri.hashCode() + kind.hashCode();
		if(!recompute){
			try {
				Type mapType = new TypeToken<Map<String, String>>(){}.getType();
				return (Map<String, String>) persistenceManager.get(key, mapType, kind);
				
			} catch (EntityNotFoundException e) {
				// compute new labelMap
			}
		}
		
		OlapHandler	olapHandler = new OlapHandler(datasetUri);
		Map<String, String> labelMap = olapHandler.getLabelMap();
		olapHandler = null;
		persistenceManager.put(key, labelMap, kind);
		
		return labelMap;
	}
	
	public Model getRdfDataset(final URL datasetUri) throws OlapException{
		OlapHandler	olapHandler = new OlapHandler(datasetUri);
		Model dataset = olapHandler.getRdfModel();
		olapHandler = null;
		return dataset;
	}
	
}
