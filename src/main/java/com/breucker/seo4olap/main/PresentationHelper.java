package com.breucker.seo4olap.main;

import java.util.List;

import org.semanticweb.yars.nx.Node;

public class PresentationHelper {

	private PresentationHelper() {}
	
	public static String generateHtmlTable(List<Node[]> result){
		
		String resultHTML = "<table class=\"table table-hover\">";
		
		boolean first = true;
		//body
		for (Node[] nodes : result) {
			if(first){
				resultHTML += "<tr>";
				for (Node node : nodes) {
					String label = node.toString();
					resultHTML += "<th>"+ label + "</th>";
				}
				resultHTML += "</tr>";
				first = false;
				continue;
			}
			resultHTML += "<tr>";
			for (Node node : nodes) {
				String label = node.toString();
				resultHTML += "<td>"+ label + "</td>";
			}
			resultHTML += "</tr>";
		}
		resultHTML += "</table>";
		return resultHTML;
	}
	
	/**
	 * Generates a simple HTML-ErrorMessage. 
	 * @return HTML-String nested in div-container
	 */
	public static String generateErrorMessage(){		
		String resultHTML = "<div>";
			resultHTML += "<h4>Error</h4>";
			resultHTML += "<p>Unfortunately an error occured while processing your request. Please try again later.</p>";
		resultHTML += "</div>";
		
		return resultHTML;
	}
	
	/**
	 * Generates a simple HTML-Message. 
	 * @return HTML-String nested in div-container
	 */
	public static String generateDatasetNotFoundMessage(){		
		String resultHTML = "<div>";
			resultHTML += "<h4>Error</h4>";
			resultHTML += "<p>The Dataset could not be found</p>";
		resultHTML += "</div>";
		
		return resultHTML;
	}
	
	public static String generateMetadata(List<Node[]> metadata){		
		String resultHTML = "<table class=\"table table-striped table-hover\">";
		
		
		boolean first = true;
		for (Node[] nodes : metadata) {
			
			if(first){
				resultHTML += "<tr>";
				for (Node node : nodes) {
					resultHTML += "<th>"+ node.toString() + "</th>";
				}
				resultHTML += "</tr>";
				first = false;
				continue;
			}
			resultHTML += "<tr>";
			for (Node node : nodes) {
				resultHTML += "<td>"+ node.toString() + "</td>";
			}
			resultHTML += "</tr>";
		}
		resultHTML += "</table>";
		
		return resultHTML;
	}
		
}
