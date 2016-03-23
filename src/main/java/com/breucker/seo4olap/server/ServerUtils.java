package com.breucker.seo4olap.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

class ServerUtils {

	private ServerUtils() {}

	public static String parameterMapToString(Map<String, String[]> map){
		if(map == null){
			return "null";
		}
		String output = "{";
		Iterator<Entry<String, String[]>> it = map.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, String[]> entry = it.next();
			output += "'" + entry.getKey() + "': [";
			for(String value: entry.getValue()){
				output += "'" + value + "', ";
			}
			output += "]";
		}
		output += "}";
		return output;
	}
}
