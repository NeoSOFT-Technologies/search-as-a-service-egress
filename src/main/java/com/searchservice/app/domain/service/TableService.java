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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.searchservice.app.domain.utils.GetCurrentSchemaUtil;
import com.searchservice.app.domain.utils.SolrDocumentUtil;
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
				d -> {
					try {
						SolrDocumentUtil myDoc = new SolrDocumentUtil();
						myDoc.putAll(d);
						validSolrDocumentsList.add(getValidMapOfDocument(myDoc.getFieldValueMap(), validColumns));
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
				});
		return validSolrDocumentsList;
	}
	
	
	public Map<String, Object> getValidMapOfDocument(Map<String, Object> mapDoc, List<String> validColumns) throws JsonProcessingException {
		Map<String, Object> map = new HashMap<>();
		for(Map.Entry<String, Object> entry: mapDoc.entrySet()) {
			String key = entry.getKey();
			if(!map.containsKey(key) && validColumns.contains(key)) {
				map.put(key, entry.getValue());
			}
		}
		return map;
	}
}