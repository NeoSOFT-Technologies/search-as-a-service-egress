package com.searchservice.app.domain.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.ResponseMessages;
import com.searchservice.app.domain.dto.SolrSearchResponseDTO;
import com.searchservice.app.domain.dto.logger.LoggersDTO;
import com.searchservice.app.domain.port.api.SolrSearchRecordsServicePort;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.rest.error.BadRequestOccurredException;
import com.searchservice.app.rest.error.NullPointerOccurredException;

@Service
@Transactional
public class SolrSearchAdvanced {
	private final Logger logger = LoggerFactory.getLogger(SolrSearchAdvanced.class);

	ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);

	private String servicename = "Search_Advanced_Service";

	private String username = "Username";

	private SolrSearchRecordsServicePort solrSearchRecordsServicePort;
	private SolrSearchResponseDTO searchResponseDTO;

	public SolrSearchAdvanced(SolrSearchRecordsServicePort solrSearchRecordsServicePort,
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

	public SolrSearchResponseDTO search(String tableName, String queryField, String queryFieldSearchTerm,
			String startRecord, String pageSize, String sortTag, String sortOrder, LoggersDTO loggersDTO) {
		logger.debug("Advanced search for the given table");

		String nameofCurrMethod = new Throwable().getStackTrace()[0].getMethodName();
		requestMethod(loggersDTO, nameofCurrMethod);
		LoggerUtils.printlogger(loggersDTO, true, false);
		searchResponseDTO = solrSearchRecordsServicePort.setUpSelectQueryAdvancedSearch(tableName, queryField,
				queryFieldSearchTerm, startRecord, pageSize, sortTag, sortOrder);
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
