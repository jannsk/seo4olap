package com.breucker.seo4olap.main;

class RuntimeContextManager {

	private RuntimeContext context;
	private static RuntimeContextManager INSTANCE = null;
	private static final ConfigurationManager configManager = ConfigurationManagerFactory.getConfigurationManager();
	
	
	private RuntimeContextManager() {}

	public static RuntimeContextManager getManager(){
		if(INSTANCE == null){
			INSTANCE = new RuntimeContextManager();
			RuntimeContext context = configManager.getContext();
			INSTANCE.setContext(context);
		}
		return INSTANCE;
	}

	
	
	public static RuntimeContext getContext() {
		if(INSTANCE == null){
			INSTANCE = new RuntimeContextManager();
			RuntimeContext context = configManager.getContext();
			INSTANCE.setContext(context);
		}
		return INSTANCE.context;
	}

	public void setContext(RuntimeContext context) {
		this.context = context;
	}
	
}
