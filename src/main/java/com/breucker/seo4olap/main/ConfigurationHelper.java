package com.breucker.seo4olap.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.olap4j.OlapException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

class ConfigurationHelper {
	
	private static final Logger logger = Logger.getLogger(PersistenceManager.class.getName());
	private ConfigurationHelper() {}

	public static void main(String[] args) throws OlapException {

	}
	
	public static Configuration getConfiguration() throws JsonSyntaxException{
		Gson gson = new Gson();
		String jsonConfig = readInConfiguration("config.json");
		Configuration config = null;
		try{
			config = gson.fromJson(jsonConfig, Configuration.class); 
		} catch(JsonSyntaxException e){
			logger.log(Level.SEVERE, "Failed reading Configuration", e);
		}
		return config;
	}

	/*#############------------####################
	 * 
	 * Private Methods
	 * 
	 *#############------------####################*/
	
	private static String readInConfiguration(String name) {
		InputStream inputStream = null;
		InputStreamReader reader = null;
		BufferedReader in = null;
		try {
			
			StreamSource stream = new StreamSource(
					ConfigurationHelper.class.getResourceAsStream("/config/" + name));
			inputStream = stream.getInputStream();
			reader = new InputStreamReader(inputStream, "UTF-8");
			in = new BufferedReader(reader);
			String config = "";
			String readString;
			while ((readString = in.readLine()) != null) {
				config += readString;
			}
			return config;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed reading Configuration",e);
		}
		finally{
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(in);
		}
		return null;
	}

}
