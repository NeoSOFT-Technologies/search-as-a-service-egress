package com.searchservice.app.domain.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.IngressSchemaResponse;
import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.domain.port.api.AdvSearchServicePort;
import com.searchservice.app.domain.port.api.SearchServicePort;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;

@Service
@Transactional
public class SearchService implements SearchServicePort {
	private static final String VERIFICATION_INCOMPLETE_MESSAGE = ", so 'multiValue' query-field verification incomplete";

	private final Logger logger = LoggerFactory.getLogger(SearchService.class);

	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	// Table service
	@Autowired
	TableService tableService;

	private AdvSearchServicePort searchRecordsServicePort;
	private SearchResponse searchResponseDTO;

	public SearchService(AdvSearchServicePort searchRecordsServicePort, SearchResponse searchResponseDTO) {
		this.searchRecordsServicePort = searchRecordsServicePort;
		this.searchResponseDTO = searchResponseDTO;
	}


	@Override
	public SearchResponse searchQuery(int tenantId, String tableName, String searchQuery, String startRecord,
			String pageSize, String sortTag, String sortOrder) {
		logger.debug("Query search for the given table");

		// Get Current Table Schema (communicating with SAAS Microservice)
		boolean isMicroserviceDown = false;
		List<String> currentListOfColumnsOfTableSchema = tableService
				.getCurrentTableSchemaColumns(tableName.split("_")[0], tenantId);
		IngressSchemaResponse currentTableSchemaResponse = tableService.getCurrentTableSchema(tableName.split("_")[0], tenantId);
		JSONArray currentTableSchema = currentTableSchemaResponse.getJsonArray();
		if (currentTableSchema == null || currentTableSchema.isEmpty())
			isMicroserviceDown = true;

		// Search documents
		searchResponseDTO = searchRecordsServicePort.setUpSelectQuerySearchViaQuery(
				currentListOfColumnsOfTableSchema, 
				tableName, 
				searchQuery, 
				startRecord, pageSize, sortTag, sortOrder);

		if (searchResponseDTO == null)
			throw new CustomException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
					HttpStatusCode.NULL_POINTER_EXCEPTION, HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		
		if(isMicroserviceDown && searchResponseDTO.getStatusCode() == 200) {
			if(!currentTableSchemaResponse.getMessage().isEmpty())
				searchResponseDTO.setMessage(
						searchResponseDTO.getMessage()
						+". "+currentTableSchemaResponse.getMessage()+VERIFICATION_INCOMPLETE_MESSAGE);
			else
				searchResponseDTO.setMessage(
						searchResponseDTO.getMessage()
						+". Couldn't interact with Ingress microservice"+VERIFICATION_INCOMPLETE_MESSAGE);
			return searchResponseDTO;
		} else if (searchResponseDTO.getStatusCode() == 200) {
			searchResponseDTO.setStatus(HttpStatus.OK);
			return searchResponseDTO;
		} else if (searchResponseDTO.getStatusCode() == HttpStatusCode.REQUEST_FORBIDDEN.getCode()) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), HttpStatusCode.BAD_REQUEST_EXCEPTION, HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage());
		} else if (searchResponseDTO.getStatusCode() == HttpStatusCode.SERVER_UNAVAILABLE.getCode()) {
			return searchResponseDTO;
		} else {
			throw new CustomException(searchResponseDTO.getStatusCode(), HttpStatusCode.getHttpStatus(searchResponseDTO.getStatusCode()),
					searchResponseDTO.getMessage());
		}

	}

	@Override
	public SearchResponse searchField(int tenantId, String tableName, String queryField, String queryFieldSearchTerm,
			String startRecord, String pageSize, String sortTag, String sortOrder) {
		logger.debug("Advanced search for the given table");

		// Get Current Table Schema (communicating with SAAS Microservice)
		boolean isMicroserviceDown = false;
		List<String> currentListOfColumnsOfTableSchema = tableService
				.getCurrentTableSchemaColumns(tableName.split("_")[0], tenantId);
		IngressSchemaResponse currentTableSchemaResponse = tableService.getCurrentTableSchema(tableName.split("_")[0], tenantId);
		JSONArray currentTableSchema = currentTableSchemaResponse.getJsonArray();
		if (currentTableSchema == null || currentTableSchema.isEmpty())
			isMicroserviceDown = true;

		// Search documents
		searchResponseDTO = searchRecordsServicePort.setUpSelectQuerySearchViaQueryField(
				currentListOfColumnsOfTableSchema, currentTableSchema, tableName, queryField, queryFieldSearchTerm,
				startRecord, pageSize, sortTag, sortOrder);

		if (searchResponseDTO == null)
			throw new CustomException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
					HttpStatusCode.NULL_POINTER_EXCEPTION, HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		
		if(isMicroserviceDown && searchResponseDTO.getStatusCode() == 200) {
			if(!currentTableSchemaResponse.getMessage().isEmpty())
				searchResponseDTO.setMessage(
						searchResponseDTO.getMessage()
						+". "+currentTableSchemaResponse.getMessage()+VERIFICATION_INCOMPLETE_MESSAGE);
			else
				searchResponseDTO.setMessage(
						searchResponseDTO.getMessage()
						+". Couldn't interact with Ingress microservice"+VERIFICATION_INCOMPLETE_MESSAGE);
			return searchResponseDTO;
		} else if (searchResponseDTO.getStatusCode() == 200) {
			searchResponseDTO.setStatus(HttpStatus.OK);
			return searchResponseDTO;
		} else if (searchResponseDTO.getStatusCode() == HttpStatusCode.REQUEST_FORBIDDEN.getCode()) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), HttpStatusCode.BAD_REQUEST_EXCEPTION, HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage());
		} else if (searchResponseDTO.getStatusCode() == HttpStatusCode.SERVER_UNAVAILABLE.getCode()) {
			return searchResponseDTO;
		} else {
			throw new CustomException(searchResponseDTO.getStatusCode(), HttpStatusCode.getHttpStatus(searchResponseDTO.getStatusCode()),
					searchResponseDTO.getMessage());
		}

	}

}
