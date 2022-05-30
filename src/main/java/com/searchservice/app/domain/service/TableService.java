package com.searchservice.app.domain.service;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpHeaders;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.searchservice.app.domain.dto.IngressSchemaResponse;
import com.searchservice.app.domain.port.api.TableServicePort;
import com.searchservice.app.domain.utils.GetCurrentSchemaUtil;
import com.searchservice.app.domain.utils.SearchDocumentUtil;
import com.searchservice.app.infrastructure.adaptor.SearchClientAdapter;


@Service
@Transactional
public class TableService implements TableServicePort {

	private static final String MICROSERVICE_INTERACT_ISSUE = "Couldn't interact with Ingress microservice to retrieve current schema details";
	
	private final Logger logger = LoggerFactory.getLogger(TableService.class);

	@Value("${base-microservice-url}")
	private String baseIngressMicroserviceUrl;
	@Value("${microservice-url.get-table}")
	private String getTableUrl;
	

	@Autowired
	SearchClientAdapter searchClientAdapter;
	
	@Autowired
	private HttpServletRequest request;

	GetCurrentSchemaUtil getCurrentSchemaUtil = new GetCurrentSchemaUtil();

	@Override
	public List<String> getCurrentTableSchemaColumns(String tableName, int tenantId) {
		logger.debug("Get current table schema from Ingress microservice");

		GetCurrentSchemaUtil getCurrentSchema = getCurrentSchemaUtilSetter(tableName, tenantId);
		GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse response = getCurrentSchema.getTable(getTokenHeaderForIngress(request));

		String responseString = response.getResponseString();

		return getCurrentSchema.getCurrentSchemaColumns(responseString);
	}

	@Override
	public IngressSchemaResponse getCurrentTableSchema(String tableName, int tenantId) {
		logger.debug("Get current table schema from Ingress microservice");
		GetCurrentSchemaUtil getCurrentSchema = getCurrentSchemaUtilSetter(tableName, tenantId);
		GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse response = getCurrentSchema.getTable(getTokenHeaderForIngress(request));

		String responseString = response.getResponseString();
		if(responseString.isEmpty())
			response.setMessage(MICROSERVICE_INTERACT_ISSUE);

		JSONArray jsonArray = getCurrentSchema.getCurrentSchemaDetails(responseString);

		return new IngressSchemaResponse(jsonArray, response.getMessage());
	}

	private GetCurrentSchemaUtil getCurrentSchemaUtilSetter(String tableName, int clientId) {
		getCurrentSchemaUtil.setBaseIngressMicroserviceUrl(baseIngressMicroserviceUrl + getTableUrl);
		getCurrentSchemaUtil.setTableName(tableName);
		getCurrentSchemaUtil.setTenantId(clientId);
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
	
	 private String getTokenHeaderForIngress(HttpServletRequest request) {
	    	String header = request.getHeader(HttpHeaders.AUTHORIZATION);
	    	if(header!=null) {
	    		return header;
	    	}else {
	    		return "";
	    	}
	    }
}
