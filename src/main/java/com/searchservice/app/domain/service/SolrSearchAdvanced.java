package com.searchservice.app.domain.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.SolrSearchResponse;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
//github.com/NeoSOFT-Technologies/search-as-a-service-egress.git
import com.searchservice.app.domain.port.api.SolrSearchRecordsServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;

@Service
@Transactional
public class SolrSearchAdvanced {
	private final Logger logger = LoggerFactory.getLogger(SolrSearchAdvanced.class);

	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	private String servicename = "Search_Advanced_Service";

	private String username = "Username";

	private SolrSearchRecordsServicePort solrSearchRecordsServicePort;
	private SolrSearchResponse searchResponseDTO;

	public SolrSearchAdvanced(SolrSearchRecordsServicePort solrSearchRecordsServicePort,
	        SolrSearchResponse searchResponseDTO) {
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
	
	public SolrSearchResponse search(
			String tableName, 
			String queryField, 
			String queryFieldSearchTerm, 
			String startRecord, 
			String pageSize, 
			String sortTag, 
			String sortOrder
			) {
		logger.debug("Advanced search for the given table");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
//		requestMethod(loggersDTO,nameofCurrMethod);
//		LoggerUtils.printlogger(loggersDTO,true,false);

		SolrSearchResponse	searchResponseDTO = solrSearchRecordsServicePort.setUpSelectQueryAdvancedSearch(tableName, queryField,
				queryFieldSearchTerm, startRecord, pageSize, sortTag, sortOrder);
//		loggersDTO.setTimestamp(LoggerUtils.utcTime().toString());
		if(searchResponseDTO != null) {
//			LoggerUtils.printlogger(loggersDTO, false, false);
			return searchResponseDTO;
		}
		else {
//			LoggerUtils.printlogger(loggersDTO,false,true);
			return searchResponseDTO;
		}
		
	}
}
