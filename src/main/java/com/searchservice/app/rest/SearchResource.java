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

import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.SolrSearchResponseDTO;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.service.SolrSearchAdvanced;
import com.searchservice.app.domain.service.SolrSearchMultifield;
import com.searchservice.app.domain.service.SolrSearchMultifieldAndMultivalue;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.infrastructure.adaptor.SolrSearchResult;
import com.searchservice.app.rest.errors.BadRequestOccurredException;

@RestController
@RequestMapping("/search/api/v1")
public class SearchResource {
    /* Solr Search Records for given collection- Egress Service Resource */
    private final Logger logger = LoggerFactory.getLogger(SearchResource.class);
    
    ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
    
    private String servicename = "Search_Resource";
    
    private String username = "Username";  

    private SolrSearchAdvanced solrSearchAdvanced;
    private SolrSearchMultifield solrSearchMultifield;
    private SolrSearchMultifieldAndMultivalue solrSearchMultifieldAndMultivalue;

    public SearchResource(
            SolrSearchAdvanced solrSearchAdvanced, 
            SolrSearchMultifield solrSearchMultifield, 
            SolrSearchMultifieldAndMultivalue solrSearchMultifieldAndMultivalue) {
        this.solrSearchAdvanced = solrSearchAdvanced;
        this.solrSearchMultifield = solrSearchMultifield;
        this.solrSearchMultifieldAndMultivalue = solrSearchMultifieldAndMultivalue;

    }

    @Autowired
    SolrSearchResult solrSearchResult;

    private void successMethod(String nameofCurrMethod, LoggersDTO loggersDTO) {
		String timestamp;
		loggersDTO.setServicename(servicename);
		loggersDTO.setUsername(username);
		loggersDTO.setNameofmethod(nameofCurrMethod);
		timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setTimestamp(timestamp);
	}
    
    @GetMapping(value = "multifield/{clientId}/{tableName}")
    public ResponseEntity<SolrSearchResponseDTO> searchRecordsInGivenTableMultifield(@PathVariable int clientId, @PathVariable String tableName,
            @RequestParam(defaultValue = "*") String queryField, @RequestParam(defaultValue = "*") String searchTerm, @RequestParam(defaultValue = "0") String startRecord,
            @RequestParam(defaultValue = "5") String pageSize, @RequestParam(defaultValue = "id") String orderBy, @RequestParam(defaultValue = "asc") String order) {
        logger.debug("REST call for MULTIFIELD SEARCH search in the given collection");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        tableName = tableName + "_" + clientId;
        SolrSearchResponseDTO solrSearchResponseDTO = solrSearchMultifield.search(clientId, tableName, queryField, searchTerm, startRecord, pageSize, orderBy, order,loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);
		
        if (solrSearchResponseDTO.getStatusCode() == 200) {
        	LoggerUtils.printlogger(loggersDTO,false,false);
            return ResponseEntity.status(HttpStatus.OK).body(solrSearchResponseDTO);
        } else {
        	LoggerUtils.printlogger(loggersDTO,false,true);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrSearchResponseDTO);
        }
    }
    
    @GetMapping(value = "multifield-multivalue/{clientId}/{tableName}")
    public ResponseEntity<SolrSearchResponseDTO> searchRecordsInGivenTableMultifieldAndMultivalue(
    		@PathVariable int clientId, 
    		@PathVariable String tableName,
            @RequestParam(defaultValue = "*") String queryField, @RequestParam(defaultValue = "*") String searchTerm, @RequestParam(defaultValue = "0") String startRecord,
            @RequestParam(defaultValue = "5") String pageSize, @RequestParam(defaultValue = "id") String orderBy, @RequestParam(defaultValue = "asc") String order) {
        logger.debug("REST call for MULTIFIELD & MULTIVALUE SEARCH search in the given collection");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        tableName = tableName + "_" + clientId;
        SolrSearchResponseDTO solrSearchResponseDTO = solrSearchMultifieldAndMultivalue.search(
        		clientId, tableName, queryField, searchTerm, startRecord, pageSize, orderBy, order,loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);
		
        if (solrSearchResponseDTO.getStatusCode() == 200) {
        	LoggerUtils.printlogger(loggersDTO,false,false);
            return ResponseEntity.status(HttpStatus.OK).body(solrSearchResponseDTO);
        } else {
        	LoggerUtils.printlogger(loggersDTO,false,true);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrSearchResponseDTO);
        }
    }
    
    @GetMapping(value = "/{clientId}/{tableName}")
    public ResponseEntity<SolrSearchResponseDTO> searchRecordsInGivenTableAdvanced(@PathVariable int clientId, @PathVariable String tableName,
            @RequestParam(defaultValue = "*") String queryField, @RequestParam(defaultValue = "*") String searchTerm, @RequestParam(defaultValue = "0") String startRecord,
            @RequestParam(defaultValue = "5") String pageSize, @RequestParam(defaultValue = "id") String orderBy, @RequestParam(defaultValue = "asc") String order) {
        logger.debug("REST call for ADVANCED SEARCH search in the given collection");

        String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		String timestamp = LoggerUtils.utcTime().toString();
		LoggersDTO loggersDTO = LoggerUtils.getRequestLoggingInfo(servicename, username,nameofCurrMethod,timestamp);
		LoggerUtils.printlogger(loggersDTO,true,false);
		loggersDTO.setCorrelationid(loggersDTO.getCorrelationid());
		loggersDTO.setIpaddress(loggersDTO.getIpaddress());
		
        tableName = tableName + "_" + clientId;
        SolrSearchResponseDTO solrSearchResponseDTO = solrSearchAdvanced.search(clientId, tableName, queryField, searchTerm, startRecord, pageSize, orderBy, order,loggersDTO);

        successMethod(nameofCurrMethod, loggersDTO);
		
        if(solrSearchResponseDTO == null){
			throw new BadRequestOccurredException(404, ResponseMessages.NULL_RESPONSE_MESSAGE);
		}
		 else if (solrSearchResponseDTO.getStatusCode() == 200) {
			LoggerUtils.printlogger(loggersDTO, false, false);
			solrSearchResponseDTO.setResponseMessage("Table Information retrieved successfully");
			return ResponseEntity.status(HttpStatus.OK).body(solrSearchResponseDTO);
		}
		
		 else{
			solrSearchResponseDTO.setStatusCode(400);
			LoggerUtils.printlogger(loggersDTO, false, true);
			solrSearchResponseDTO.setResponseMessage(ResponseMessages.BAD_REQUEST_MSG);
			throw new BadRequestOccurredException(400,"REST operation couldn't be performed");
		} 

    }
}