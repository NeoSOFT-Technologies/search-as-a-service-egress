package com.searchservice.app.domain.port.api;

import java.util.List;

import com.searchservice.app.domain.dto.SolrSearchResponseDTO;

public interface SolrSearchRecordsServicePort {
	SolrSearchResponseDTO setUpSelectQueryUnfiltered(	
										String collection);
	
	SolrSearchResponseDTO setUpSelectQueryBasicSearch(	
										String collection, 
										String queryField, 
										String searchTerm);
	
	SolrSearchResponseDTO setUpSelectQueryOrderedSearch(	
										String collection, 
										String queryField, 
										String searchTerm, 
										String tag, 
										String order);
	
	SolrSearchResponseDTO setUpSelectQueryAdvancedSearch(
										List<String> validSchemaColumns, 
										String collection, 
										String queryField, 
										String searchTerm, 
										String startRecord, 
										String pageSize, 
										String tag, 
										String order);
	
	SolrSearchResponseDTO setUpSelectQueryAdvancedSearchWithPagination(	
										String collection, 
										String queryField, 
										String searchTerm, 
										String startRecord, 
										String pageSize, 
										String tag, String order,
										String startPage);
}
