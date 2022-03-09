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

import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.domain.dto.logger.Loggers;
import com.searchservice.app.domain.port.api.SearchServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

@Service
@Transactional
public class SearchViaQueryField {
	private final Logger logger = LoggerFactory.getLogger(SearchViaQueryField.class);

	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	private String servicename = "Search_Advanced_Service";

	private String username = "Username";

	// Table service
	@Autowired
	TableService tableService;
	
	private SearchServicePort solrSearchRecordsServicePort;
	private SearchResponse searchResponseDTO;

	public SearchViaQueryField(SearchServicePort solrSearchRecordsServicePort,
			SearchResponse searchResponseDTO) {
		this.solrSearchRecordsServicePort = solrSearchRecordsServicePort;
		this.searchResponseDTO = searchResponseDTO;
	}

	private void requestMethod(Loggers loggersDTO, String nameofCurrMethod) {

		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod(nameofCurrMethod);
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
	}
	
	public SearchResponse search(int clientId, String tableName, String queryField, String queryFieldSearchTerm,
			String startRecord, String pageSize, String sortTag, String sortOrder, Loggers loggersDTO) {
		logger.debug("Advanced search for the given table");

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
		searchResponseDTO = solrSearchRecordsServicePort.setUpSelectQuerySearchViaQueryField(
				currentListOfColumnsOfTableSchema, 
				currentTableSchema, 
				tableName, 
				queryField,
				queryFieldSearchTerm,				
				startRecord, pageSize, sortTag, sortOrder);
		if(isMicroserviceDown)
			searchResponseDTO.setResponseMessage(
					searchResponseDTO.getResponseMessage()
					+". Microservice is down, so 'multiValue' query-field verification incomplete; will be treated as single-valued for now");
		loggersDTO.setTimestamp(LoggerUtils.utcTime().toString());
		
		if (searchResponseDTO == null)
			throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);
		else if (searchResponseDTO.getStatusCode() == 200) {
			LoggerUtils.printlogger(loggersDTO, false, false);
			return searchResponseDTO;
		} else {
			searchResponseDTO.setStatusCode(400);
			LoggerUtils.printlogger(loggersDTO, false, true);
			throw new BadRequestOccurredException(400, ResponseMessages.BAD_REQUEST_MSG);
		}
		
	}
}
