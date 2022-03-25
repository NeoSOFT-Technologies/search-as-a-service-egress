package com.searchservice.app.domain.port.api;

import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;

import com.fasterxml.jackson.core.JsonProcessingException;


public interface TableServicePort {	
	List<String> getCurrentTableSchemaColumns(String tableName, int clientId);
	
	JSONArray getCurrentTableSchema(String tableName, int clientId);
	
	 List<Map<String, Object>> getValidDocumentsList(SolrDocumentList docs, List<String> validColumns);
	 
	 Map<String, Object> getValidMapOfDocument(Map<String, Object> mapDoc, List<String> validColumns) throws JsonProcessingException;
}