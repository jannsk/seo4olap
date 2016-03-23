package com.breucker.seo4olap.main;

public class RuntimeContext {

	private boolean isDebugMode = false;
	
	public RuntimeContext() {}

	public boolean isDebugMode() {
		return isDebugMode;
	}

	public void setDebugMode(boolean isDebugMode) {
		this.isDebugMode = isDebugMode;
	}
	
}
