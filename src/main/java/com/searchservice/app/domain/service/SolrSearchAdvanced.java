package com.searchservice.app.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.SolrSearchResponse;
import com.searchservice.app.domain.port.api.SolrSearchRecordsServicePort;

@Service
@Transactional
public class SolrSearchAdvanced {
	private final Logger logger = LoggerFactory.getLogger(SolrSearchAdvanced.class);
	
	private SolrSearchRecordsServicePort solrSearchRecordsServicePort;
	public SolrSearchAdvanced(SolrSearchRecordsServicePort solrSearchRecordsServicePort) {
		this.solrSearchRecordsServicePort = solrSearchRecordsServicePort;
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
		return solrSearchRecordsServicePort.setUpSelectQueryAdvancedSearch(
				tableName, 
				queryField, 
				queryFieldSearchTerm, 
				startRecord, 
				pageSize, 
				sortTag, 
				sortOrder);
	}
}
