package com.breucker.seo4olap.olap;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.olap4j.driver.olap4ld.helper.Olap4ldLinkedDataUtil;
import org.semanticweb.yars.nx.Node;

public class OlapHelper {
	
	private static final Logger logger = Logger.getLogger(OlapHandler.class.getName());	
	
	private OlapHelper() {
		// TODO Auto-generated constructor stub
	}

	public static void printMetadataToLogger(final List<Node[]> metadata, final String name, final String identifier){
//		if(metadata != null && identifier != null){
//			if(name != null){
//				logger.info(name);
//			}
//			Map<String, Integer> map = getMetadataMap(metadata);
//			Integer field = map.get(identifier);
//			if(field != null){
//				for (Node[] nodes : metadata) {
//					logger.info(nodes[field].toString());
//				}	
//			}
//		}	
		for(Node[] data: metadata){
			String output = "###" + name + "###: ";
			for(int i=0; i< data.length; i++){
				output += data[i].toString() +  "  ";
			}
			logger.info(output);
		}
	}
	
	public static Map<String, Integer> getMetadataMap(List<Node[]> metadata){
		return Olap4ldLinkedDataUtil.getNodeResultFields(metadata.get(0));
	}
	
	public static void printMetadata(List<Node[]> metadata, String name){
		for(Node[] data: metadata){
			String output = "###" + name + "###: ";
			for(int i=0; i< data.length; i++){
				output += data[i].toString() +  "  ";
			}
			System.out.println(output);
		}
	}
	
	public static void printOlapResult(OlapResult olapResult){
		System.out.println("### print OlapResult ###: ");
//		printMetadata(olapResult.getResult(), "result");
//		Map<String, ResultHeaderObject> headerMap = olapResult.getHeaderObjectMap();
//		Iterator<String> it = headerMap.keySet().iterator();
//		while(it.hasNext()){
//			String key = it.next();
//			ResultHeaderObject object = headerMap.get(key);
//			System.out.println("key: '" + key + "' " + object.toString());
//		}
	}
	
	public static void printStringList(List<String> list, String name){
		String output = "";
		for(String text: list){
			output += " '" + text + "' ";
		}

		System.out.println("###########" + name + "########## " + output);
	}
	
	public static void printStringArray(String[] list, String name){
		String output = "";
		for(String text: list){
			output += " '" + text + "' ";
		}

		System.out.println("###########" + name + "########## " + output);
	}
}
