package com.breucker.seo4olap.olap;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.log4j.varia.NullAppender;
import org.olap4j.OlapException;
import org.olap4j.driver.olap4ld.Olap4ldUtil;
import org.olap4j.driver.olap4ld.linkeddata.Restrictions;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;

import com.breucker.seo4olap.olap4ld.OlapSesameEngine;

/**
 * Factory for managing Singleton instances of LinkedDataCubesEngines
 * 
 * @author Daniel Breucker
 *
 */
class OlapEngineFactory {

	private static final Logger logger = Logger.getLogger(OlapEngineFactory.class.getName());
	private static Map<Integer, OlapSesameEngine> existingEngines = new HashMap<Integer, OlapSesameEngine>();
	private static boolean isOlap4LdLoggerOn = false;
	
	private OlapEngineFactory() {}

	/**
	 * Returns a LinkedDataCubesEngine filled with Data from dataSetUri
	 * @param dataSetUri
	 * @return LinkedDataCubesEngine
	 * @throws OlapException in case that DataSet did not fulfill requirements
	 */
	public static OlapSesameEngine getEngine(final String dataSetUri) throws OlapException {
		logger.info("LDCE get engine dsuri.hashcode: " + dataSetUri.hashCode());
		//on first time, initiate OLAP4LD-Logger
		if (!isOlap4LdLoggerOn){
			logger.info("LDCE get engine init logger");
			//Olap4ld uses log4j. We need this lines in order to not get Warnings.
			//The real logging setup is made with GAE in logging.properties
			Olap4ldUtil._log = Logger.getLogger("Olap4ldDriver");
			org.apache.log4j.BasicConfigurator.configure(new NullAppender());
			isOlap4LdLoggerOn = true;
		}
		// return existing DataCubesEngine
		if (existingEngines.containsKey(dataSetUri.hashCode())){
			logger.info("LDCE get engine return existing engine dsuri: " + dataSetUri);
			return existingEngines.get(dataSetUri.hashCode());
		}
		// create new DataCubesEngine
		else{
			logger.info("LDCE get engine return new engine dsuri: " + dataSetUri);
			//create new Engine			
			OlapSesameEngine lde = null;
			lde = new OlapSesameEngine();
			
			//fill Engine with Data
			Restrictions restrictions = new Restrictions();
			Node dsUriNode = new Resource(dataSetUri);
			restrictions.cubeNamePattern = dsUriNode;
			lde.getCubes(restrictions);
			
			//Store Engine in Map
			existingEngines.put(dataSetUri.hashCode(), lde);
			
			return lde;
		}
	}
}
