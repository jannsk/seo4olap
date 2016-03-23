package com.breucker.seo4olap.main;

import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;

/**
 * PersistenceManager manages Data storage and retrieval. 
 * Stored objects should be serializable in json-Format.
 * 
 * @author Daniel Breucker
 *
 */
class PersistenceManager {

	private static final Logger logger = Logger.getLogger(PersistenceManager.class.getName());	
	private Cache cache = null;
	private final DatastoreService datastore;
	private final Gson gson = new Gson();
	private final ConfigurationManager configManager;
	private final String domain;
	
	/**
	 * PersistenceManager manages Data storage and retrieval. 
	 * Stored objects should be serializable in json-Format.
	 * 
	 */
	public PersistenceManager() {
		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
		    cache = cacheFactory.createCache(Collections.emptyMap());
		} catch (CacheException e) {
			//cache stays null
		}
		this.datastore = DatastoreServiceFactory.getDatastoreService();
		this.configManager = ConfigurationManagerFactory.getConfigurationManager();
		this.domain = configManager.getBaseUri() + "/" + configManager.getVersion();
	}

	/**
	 * Store an Object to Persistence Layer.
	 * Keys are generated through key.hashCode(). Make sure the KeyObject has a proper hashCode()-Function
	 * @param key a key
	 * @param value the Object to persist.  
	 * @param kind Any String describing the valueObject. key and kind define the final key
	 */
	@SuppressWarnings("unchecked")
	public void put(Object key, Object value, String kind){
		if(key == null || value == null || kind == null){
			throw new InvalidParameterException("Inputparameter cannot be null");
		}
		kind = this.domain + "-" + kind;
		int storeKey = key.hashCode() + kind.hashCode();
		
		String jsonResultString = gson.toJson(value);
		int length = jsonResultString.getBytes().length;
		
		//max string size of cache and datastore is 1MB
		if(length < 1000000){
			logger.info("Put result to datastore. key: " + storeKey + " kind: " + kind);
			Text jsonResultText = new Text(jsonResultString);
			Entity resultEntity = new Entity(kind, storeKey);
			resultEntity.setProperty("json", jsonResultText);
			datastore.put(resultEntity);
			if(cache != null){
				cache.put(storeKey, jsonResultString);
			}
		}
		
	}
	
	/**
	 * Retrieve an Object from Persistence Layer
	 * @param key a key
	 * @param valueType The Class of the returned valueObject
	 * @param kind Any String describing the valueObject. key and kind define the final key
	 * @return valueObject
	 * @throws EntityNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public Object get(Object key, Type valueType, String kind) throws EntityNotFoundException{
		if(kind == null){
			kind = "default";
		}
		kind = this.domain + "-" + kind;
		int storeKey = key.hashCode() + kind.hashCode();
		
		if(cache != null && cache.containsKey(storeKey)){
			logger.info("Retrieve result from cache. key: " + storeKey + " kind: " + kind);
			String json = (String) cache.get(storeKey);
			return gson.fromJson(json, valueType);
		}
		Key datastoreKey = KeyFactory.createKey(kind, storeKey);
		Entity resultEntity = datastore.get(datastoreKey);
		logger.info("Retrieve result from datastore. key: " + storeKey + " kind: " + kind);
		Text jsonText = (Text) resultEntity.getProperty("json");
		String json = jsonText.getValue();
		//store in Cache
		if(cache != null){
			cache.put(storeKey, json);
		}
		return gson.fromJson(json, valueType);
	}
	
	public boolean delete(Object key, String kind){
		boolean result = false;
		if(key == null || kind == null){
			return result;
		}
		int storeKey = key.hashCode() + kind.hashCode();
		if(cache != null && cache.containsKey(storeKey)){
			logger.info("Removed object from Cache. key: " + storeKey + " kind: " + kind);
			cache.remove(storeKey);
		}
		Key datastoreKey = KeyFactory.createKey(kind, storeKey);
		try{
			datastore.delete(datastoreKey);
			result = true;
			logger.info("Removed object from Datastore. key: " + storeKey + " kind: " + kind);
		} catch(Exception e){
			logger.warning("Failed to remove Object from datastore. key: " + storeKey + " kind: " + kind);
		}
		return result;
	}
	
	public void clearCache(){
		if(cache != null){
			cache.clear();
		}
		logger.info("Cache cleared");
	}
}
