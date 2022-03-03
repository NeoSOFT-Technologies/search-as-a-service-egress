package com.searchservice.app.domain.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.SearchResponseDTO;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.port.api.SearchServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.searchservice.app.rest.errors.NullPointerOccurredException;

@Service
@Transactional
public class SearchViaQuery {
	private final Logger logger = LoggerFactory.getLogger(SearchViaQuery.class);

	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	private String servicename = "Search_Via_Query_Service";

	private String username = "Username";

	// Table service
	@Autowired
	TableService tableService;
	
	private SearchServicePort solrSearchRecordsServicePort;
	private SearchResponseDTO searchResponseDTO;

	public SearchViaQuery(SearchServicePort solrSearchRecordsServicePort,
			SearchResponseDTO searchResponseDTO) {
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
	
	public SearchResponseDTO search(
			int clientId, String tableName, 
			String searchQuery, 
			String startRecord, String pageSize, String sortTag, String sortOrder, LoggersDTO loggersDTO) {
		logger.debug("Query search for the given table");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO,nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO,true,false);

		// Get Current Table Schema (communicating with SAAS Microservice)
		List<String> currentListOfColumnsOfTableSchema = tableService.getCurrentTableSchemaColumns(tableName.split("_")[0], clientId);
		searchResponseDTO = solrSearchRecordsServicePort.setUpSelectQuerySearchViaQuery(
				currentListOfColumnsOfTableSchema, 
				tableName, 
				searchQuery, 
				startRecord, pageSize, sortTag, sortOrder);
		loggersDTO.setTimestamp(LoggerUtils.utcTime().toString());
		if (searchResponseDTO == null) {
			LoggerUtils.printlogger(loggersDTO, false, true);
			throw new NullPointerOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);
		} else if (searchResponseDTO.getStatusCode() == 200) {
			LoggerUtils.printlogger(loggersDTO, false, false);
			return searchResponseDTO;
		} else {
			searchResponseDTO.setStatusCode(400);
			LoggerUtils.printlogger(loggersDTO, false, true);
			//throw new BadRequestOccurredException(400, ResponseMessages.BAD_REQUEST_MSG);
			return searchResponseDTO;
		}
	}
}
