package com.searchservice.app.domain.service;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.searchservice.app.domain.port.api.TableServicePort;
import com.searchservice.app.domain.utils.GetCurrentSchemaUtil;
import com.searchservice.app.domain.utils.SearchDocumentUtil;
import com.searchservice.app.infrastructure.adaptor.SearchClientAdapter;


@Service
@Transactional
public class TableService implements TableServicePort {

	private final Logger logger = LoggerFactory.getLogger(TableService.class);

	@Value("${base-microservice-url}")
	private String baseIngressMicroserviceUrl;
	@Value("${microservice-url.get-table}")
	private String getTableUrl;

	String message = "";

	@Autowired
	SearchClientAdapter searchClientAdapter;

	GetCurrentSchemaUtil getCurrentSchemaUtil = new GetCurrentSchemaUtil();

	@Override
	public List<String> getCurrentTableSchemaColumns(String tableName, int clientId) {
		logger.debug("Get current table schema from Ingress microservice");

		GetCurrentSchemaUtil getCurrentSchema = extracted(tableName, clientId);
		GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse response = getCurrentSchema.get();

		String responseString = response.getResponseString();

		return getCurrentSchema.getCurrentSchemaColumns(responseString);
	}

	@Override
	public JSONArray getCurrentTableSchema(String tableName, int clientId) {
		logger.debug("Get current table schema from Ingress microservice");

		GetCurrentSchemaUtil getCurrentSchema = extracted(tableName, clientId);
		GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse response = getCurrentSchema.get();

		String responseString = response.getResponseString();

		return getCurrentSchema.getCurrentSchemaDetails(responseString);
	}

	private GetCurrentSchemaUtil extracted(String tableName, int clientId) {

		getCurrentSchemaUtil.setBaseIngressMicroserviceUrl(baseIngressMicroserviceUrl + getTableUrl);
		getCurrentSchemaUtil.setTableName(tableName);
		getCurrentSchemaUtil.setClientId(clientId);

		return getCurrentSchemaUtil;
	}

	@Override
	public List<Map<String, Object>> getValidDocumentsList(SolrDocumentList docs, List<String> validColumns) {
		logger.debug("Validate table documents");

		List<Map<String, Object>> validSearchDocumentsList = new ArrayList<>();
		docs.forEach(d -> {
			try {
				SearchDocumentUtil myDoc = new SearchDocumentUtil();
				myDoc.putAll(d);
				validSearchDocumentsList.add(getValidMapOfDocument(myDoc.getFieldValueMap(), validColumns));
			} catch (JsonProcessingException e) {
				logger.error("Error occurred while retrieving map of document with valid columns. ", e);
			}
		});
		return validSearchDocumentsList;
	}

	@Override
	public Map<String, Object> getValidMapOfDocument(Map<String, Object> mapDoc, List<String> validColumns)
			throws JsonProcessingException {
		Map<String, Object> map = new HashMap<>();
		for (Map.Entry<String, Object> entry : mapDoc.entrySet()) {
			String key = entry.getKey();
			if (!map.containsKey(key) && validColumns.contains(key)) {
				map.put(key, entry.getValue());
			}
		}
		return map;
	}
}
