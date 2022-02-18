package com.searchservice.app.domain.port.api;

import java.util.List;

import com.searchservice.app.domain.dto.SolrSearchResponseDTO;

public interface SolrSearchRecordsServicePort {
	
	SolrSearchResponseDTO setUpSelectQueryAdvancedSearch(
										List<String> validSchemaColumns, 
										String collection, 
										String queryField, 
										String searchTerm, 
										String startRecord, 
										String pageSize, 
										String tag, 
										String order);
}
