package com.searchservice.app.rest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.domain.dto.logger.Loggers;
import com.searchservice.app.domain.service.SearchViaQueryField;
import com.searchservice.app.domain.service.SearchViaQuery;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.domain.utils.SearchUtil;
import com.searchservice.app.infrastructure.adaptor.SearchResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("${base-url.api-endpoint.home}")
public class SearchResource {
    /* Search Records for given collection- Egress Service Resource ***/
    private final Logger logger = LoggerFactory.getLogger(SearchResource.class);
    
    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
    
    private String servicename = "Search_Resource";
    private String username = "Username";

    private SearchViaQueryField searchViaQueryField;
    private SearchViaQuery searchViaQuery;

    public SearchResource(
            SearchViaQueryField searchViaQueryField, 
            SearchViaQuery searchViaQuery) {
        this.searchViaQueryField = searchViaQueryField;
        this.searchViaQuery = searchViaQuery;
    }

    @Autowired
    SearchResult searchResult;

    private void successMethod(String nameofCurrMethod, Loggers loggersDTO) {
		String timestamp;
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
	}
    
    
    @GetMapping(value = "/{tenantId}/{tableName}")
    @Operation(summary = "GET RECORDS" ,security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SearchResponse> searchRecordsViaQueryField(
    		@PathVariable int tenantId, 
    		@PathVariable String tableName, 
            @RequestParam(defaultValue = "*") String queryField, @RequestParam(defaultValue = "*") String searchTerm, 
            @RequestParam(defaultValue = "0") String startRecord,
            @RequestParam(defaultValue = "5") String pageSize, 
            @RequestParam(defaultValue = "id") String orderBy, @RequestParam(defaultValue = "asc") String order) {
        logger.debug("REST call for records-search in the given table");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		Loggers loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());

		// Validate inputs
		SearchUtil.validateInputs(startRecord, pageSize, order);

        tableName = tableName + "_" + tenantId;
        SearchResponse searchResponseDTO = searchViaQueryField.search(
        		tenantId, tableName, 
        		queryField, searchTerm, 
        		startRecord, pageSize, orderBy, order,loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);
		
        if (searchResponseDTO.getStatusCode() == 200) {
        	LoggerUtils.printlogger(loggersDTO,false,false);
            return ResponseEntity.status(HttpStatus.OK).body(searchResponseDTO);
        } else {
        	LoggerUtils.printlogger(loggersDTO,false,true);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(searchResponseDTO);
        }
    }
    
    
    @GetMapping(value = "/query/{tenantId}/{tableName}")
    @Operation(summary = "GET RECORDS VIA QUERY" ,security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SearchResponse> searchRecordsViaQuery(
    		@PathVariable int tenantId, 
    		@PathVariable String tableName, 
            @RequestParam(defaultValue = "*") String searchQuery, 
            @RequestParam(defaultValue = "0") String startRecord,
            @RequestParam(defaultValue = "5") String pageSize, 
            @RequestParam(defaultValue = "id") String orderBy, @RequestParam(defaultValue = "asc") String order) {
        logger.debug("REST call for records-search in the given table");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		Loggers loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
				
		// Validate inputs
		SearchUtil.validateInputs(startRecord, pageSize, order);
		
        tableName = tableName + "_" + tenantId;
        SearchResponse searchResponseDTO = searchViaQuery.search(
        		tenantId, tableName, 
        		searchQuery, 
        		startRecord, pageSize, orderBy, order,loggersDTO);
        
        successMethod(nameofCurrMethod, loggersDTO);
		
        if (searchResponseDTO.getStatusCode() == 200) {
        	LoggerUtils.printlogger(loggersDTO,false,false);
            return ResponseEntity.status(HttpStatus.OK).body(searchResponseDTO);
        } else {
        	LoggerUtils.printlogger(loggersDTO,false,true);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(searchResponseDTO);
        }
    }
    
}