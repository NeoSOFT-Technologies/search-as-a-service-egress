package com.searchservice.app.domain.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.SolrSearchResponseDTO;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.port.api.SolrSearchRecordsServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;

@Service
@Transactional
public class SolrSearch {
	private final Logger logger = LoggerFactory.getLogger(SolrSearch.class);

	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	private String servicename = "Search_Multifield_Service";

	private String username = "Username";

	// Table service
	@Autowired
	TableService tableService;
	
	private SolrSearchRecordsServicePort solrSearchRecordsServicePort;
	private SolrSearchResponseDTO searchResponseDTO;

	public SolrSearch(SolrSearchRecordsServicePort solrSearchRecordsServicePort,
			SolrSearchResponseDTO searchResponseDTO) {
		this.solrSearchRecordsServicePort = solrSearchRecordsServicePort;
		this.searchResponseDTO = searchResponseDTO;
	}

	private void requestMethod(LoggersDTO loggersDTO, String nameofCurrMethod) {

		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
	}
	
	public SolrSearchResponseDTO search(int clientId, String tableName, String queryField, String queryFieldSearchTerm, String searchOperator,
			String startRecord, String pageSize, String sortTag, String sortOrder, LoggersDTO loggersDTO) {
		logger.debug("Multifield search for the given table");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO,nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO,true,false);

		// Get Current Table Schema (communicating with SAAS Microservice)
		boolean isMicroserviceDown = false;
		List<String> currentListOfColumnsOfTableSchema = tableService.getCurrentTableSchemaColumns(tableName.split("_")[0], clientId);
		JSONArray currentTableSchema = tableService.getCurrentTableSchema(tableName.split("_")[0], clientId);

		if(currentTableSchema.isEmpty())
			isMicroserviceDown = true;
		
		// Search documents
		searchResponseDTO = solrSearchRecordsServicePort.setUpSelectQuery(
				currentListOfColumnsOfTableSchema, 
				currentTableSchema, 
				tableName, queryField,
				queryFieldSearchTerm, 
				searchOperator, 
				startRecord, pageSize, sortTag, sortOrder);
		if(isMicroserviceDown)
			searchResponseDTO.setResponseMessage(
					searchResponseDTO.getResponseMessage()
					+". Microservice is down");
		
		loggersDTO.setTimestamp(LoggerUtils.utcTime().toString());
		if(searchResponseDTO != null) {
			LoggerUtils.printlogger(loggersDTO, false, false);
			return searchResponseDTO;
		}
		else {
			LoggerUtils.printlogger(loggersDTO,false,true);
			return searchResponseDTO;
		}
		
	}
}
