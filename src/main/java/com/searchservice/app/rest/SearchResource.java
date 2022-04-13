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
import com.searchservice.app.domain.port.api.SearchServicePort;
import com.searchservice.app.domain.service.SearchService;
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
    
    private SearchServicePort searchservice;

    public SearchResource(    	
            SearchService searchservice) {      
        this.searchservice = searchservice;
    }

    @Autowired
    SearchResult searchResult;
    
    @GetMapping(value = "/{tenantId}/{tableName}")
    @Operation(summary = "GET RECORDS BASED ON A SPECIFIC COLUMN AND ITS VALUE" ,security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SearchResponse> searchRecordsViaQueryField(
    		@PathVariable int tenantId, 
    		@PathVariable String tableName, 
            @RequestParam(defaultValue = "*") String queryField, @RequestParam(defaultValue = "*") String searchTerm, 
            @RequestParam(defaultValue = "0") String startRecord,
            @RequestParam(defaultValue = "5") String pageSize, 
            @RequestParam(defaultValue = "id") String orderBy, @RequestParam(defaultValue = "asc") String order) {
        logger.debug("REST call for records-search in the given table");

		// Validate inputs
		SearchUtil.validateInputs(startRecord, pageSize, order);

        tableName = tableName + "_" + tenantId;
        SearchResponse searchResponseDTO = searchservice.searchField(
        		tenantId, tableName, 
        		queryField, searchTerm, 
        		startRecord, pageSize, orderBy, order);

		
        if (searchResponseDTO.getStatusCode() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(searchResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(searchResponseDTO);
        }
    }
    
    
    @GetMapping(value = "/query/{tenantId}/{tableName}")
    @Operation(summary = "GET RECORDS WITH THE HELP OF CUSTOM QUERY" ,security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<SearchResponse> searchRecordsViaQuery(
    		@PathVariable int tenantId, 
    		@PathVariable String tableName, 
            @RequestParam(defaultValue = "*") String searchQuery, 
            @RequestParam(defaultValue = "0") String startRecord,
            @RequestParam(defaultValue = "5") String pageSize, 
            @RequestParam(defaultValue = "id") String orderBy, @RequestParam(defaultValue = "asc") String order) {
        logger.debug("REST call for records-search in the given table");
				
		// Validate inputs
		SearchUtil.validateInputs(startRecord, pageSize, order);
		
        tableName = tableName + "_" + tenantId;
        SearchResponse searchResponseDTO = searchservice.searchQuery(
        		tenantId, tableName, 
        		searchQuery, 
        		startRecord, pageSize, orderBy, order);
       
        if (searchResponseDTO.getStatusCode() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(searchResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(searchResponseDTO);
        }
    }
    
}