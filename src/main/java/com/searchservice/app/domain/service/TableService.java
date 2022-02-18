package com.searchservice.app.domain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.utils.GetCurrentSchemaUtil;
import com.searchservice.app.infrastructure.adaptor.SolrAPIAdapter;

@Service
@Transactional
public class TableService {

	private final Logger logger = LoggerFactory.getLogger(TableService.class); 
	
	@Value("${base-microservice-url}")
	private String baseIngressMicroserviceUrl;
	@Value("${microservice-url.get-table}")
	private String getTableUrl;
	
	@Autowired
	SolrAPIAdapter solrAPIAdapter = new SolrAPIAdapter();

	
	public List<String> getCurrentTableSchema(String tableName, int clientId) {
		logger.debug("Get current table schema from Ingress microservice");
		
		GetCurrentSchemaUtil getCurrentSchemaUtil = extracted(tableName, clientId);
		GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse response = getCurrentSchemaUtil.get();
		String responseString = response.getResponseString();
		
		return getCurrentSchemaUtil.getCurrentSchemaColumns(responseString);
	}
	

	private GetCurrentSchemaUtil extracted(String tableName, int clientId) {
		GetCurrentSchemaUtil getCurrentSchemaUtil = new GetCurrentSchemaUtil();
		getCurrentSchemaUtil.setBaseIngressMicroserviceUrl(baseIngressMicroserviceUrl + getTableUrl);
		getCurrentSchemaUtil.setTableName(tableName);
		getCurrentSchemaUtil.setClientId(clientId);
		
		return getCurrentSchemaUtil;
	}
	
	
	public List<Map<String, Object>> getValidDocumentsList(SolrDocumentList docs, List<String> validColumns) {
		logger.debug("Validate table documents");
		
		List<Map<String, Object>> validSolrDocumentsList = new ArrayList<>();
		docs.forEach(
				d -> validSolrDocumentsList.add(getValidMapOfDocument(d.getFieldValueMap(), validColumns)));
		return validSolrDocumentsList;
	}
	
	
	public Map<String, Object> getValidMapOfDocument(Map<String, Object> mapDoc, List<String> validColumns) {
		Map<String, Object> map = new HashMap<>();
		// Iterating over entrySet() is not allowed by SolrDocument class
		for(String col: mapDoc.keySet()) {
			if(!map.containsKey(col) && validColumns.contains(col)) {
				map.put(col, mapDoc.get(col));
			}	
		}
		return map;
	}
}
