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
import com.searchservice.app.domain.utils.HttpStatusCode;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.CustomException;

@Service
@Transactional
public class SearchService implements SearchServicePort {
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
		List<String> currentListOfColumnsOfTableSchema = tableService.getCurrentTableSchemaColumns(tableName.split("_")[0], tenantId);
		searchResponseDTO = searchRecordsServicePort.setUpSelectQuerySearchViaQuery(
				currentListOfColumnsOfTableSchema, 
				tableName, 
				searchQuery, 
				startRecord, pageSize, sortTag, sortOrder);


		if (searchResponseDTO == null) {
			throw new CustomException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), HttpStatusCode.NULL_POINTER_EXCEPTION,HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		} else if (searchResponseDTO.getStatusCode() == 200) {
			return searchResponseDTO;
		} else if (searchResponseDTO.getStatusCode() == 503) {
			return searchResponseDTO;
		} else {
			searchResponseDTO.setStatusCode(400);

			return searchResponseDTO;
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
		if (currentTableSchema.isEmpty())
			isMicroserviceDown = true;

		// Search documents
		searchResponseDTO = searchRecordsServicePort.setUpSelectQuerySearchViaQueryField(
				currentListOfColumnsOfTableSchema, currentTableSchema, tableName, queryField, queryFieldSearchTerm,
				startRecord, pageSize, sortTag, sortOrder);

		if(isMicroserviceDown && searchResponseDTO.getStatusCode() == 200) {
			if(!currentTableSchemaResponse.getMessage().isEmpty())
				searchResponseDTO.setMessage(
						searchResponseDTO.getMessage()
						+". "+currentTableSchemaResponse.getMessage()+", so 'multiValue' query-field verification incomplete; will be treated as single-valued for now");
			else
				searchResponseDTO.setMessage(
						searchResponseDTO.getMessage()
						+". Couldn't interact with Ingress microservice, so 'multiValue' query-field verification incomplete; will be treated as single-valued for now");
		}	
		if (searchResponseDTO == null)
			throw new CustomException(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), HttpStatusCode.NULL_POINTER_EXCEPTION,HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		else if (searchResponseDTO.getStatusCode() == 200) {
			searchResponseDTO.setStatus(HttpStatus.OK);
			return searchResponseDTO;
		} else if (searchResponseDTO.getStatusCode() == 403) {
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),HttpStatusCode.BAD_REQUEST_EXCEPTION, searchResponseDTO.getMessage());
		} else if (searchResponseDTO.getStatusCode() == 503) {
			return searchResponseDTO;
		} else {
			searchResponseDTO.setStatusCode(400);
			throw new CustomException(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(),HttpStatusCode.BAD_REQUEST_EXCEPTION, HttpStatusCode.BAD_REQUEST_EXCEPTION.getMessage());
		}

	}

}
