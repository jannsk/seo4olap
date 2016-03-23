package com.breucker.seo4olap.main;

public class ConfigurationManagerFactory {

	private static ConfigurationManager configManager = new ConfigurationManager();
	
	private ConfigurationManagerFactory() {}
	
	public static ConfigurationManager getConfigurationManager(){
		return configManager;
	}

}
