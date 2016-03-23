package com.breucker.seo4olap.olap;

import java.util.List;
import java.util.Map;

import org.semanticweb.yars.nx.Node;

public class OlapResult {
	
	private OlapResultInformation resultInformation = null;
	private Map<String, OlapResultColumn> columns = null;
	private List<Node[]> rows = null;
	private Map<String, OlapResultMember> memberMap = null;
	

	public OlapResult() {}

	public OlapResult(OlapResultInformation resultInformation, Map<String, OlapResultColumn> columns, List<Node[]> rows,
			Map<String, OlapResultMember> memberMap) {
		this.resultInformation = resultInformation;
		this.columns = columns;
		this.rows = rows;
		this.memberMap = memberMap;
	}

	public OlapResultInformation getResultInformation() {
		return resultInformation;
	}

	public void setResultInformation(OlapResultInformation resultInformation) {
		this.resultInformation = resultInformation;
	}

	public Map<String, OlapResultColumn> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, OlapResultColumn> columns) {
		this.columns = columns;
	}

	public List<Node[]> getRows() {
		return rows;
	}

	public void setRows(List<Node[]> rows) {
		this.rows = rows;
	}

	public Map<String, OlapResultMember> getMemberMap() {
		return memberMap;
	}

	public void setMemberMap(Map<String, OlapResultMember> memberMap) {
		this.memberMap = memberMap;
	}

}