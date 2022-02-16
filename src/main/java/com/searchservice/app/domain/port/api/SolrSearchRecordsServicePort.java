package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.SolrSearchResponse;

public interface SolrSearchRecordsServicePort {
	SolrSearchResponse setUpSelectQueryUnfiltered(	
										String collection);
	
	SolrSearchResponse setUpSelectQueryBasicSearch(	
										String collection, 
										String queryField, 
										String searchTerm);
	
	SolrSearchResponse setUpSelectQueryOrderedSearch(	
										String collection, 
										String queryField, 
										String searchTerm, 
										String tag, 
										String order);
	
	SolrSearchResponse setUpSelectQueryAdvancedSearch(	
										String collection, 
										String queryField, 
										String searchTerm, 
										String startRecord, 
										String pageSize, 
										String tag, 
										String order);
	
	SolrSearchResponse setUpSelectQueryAdvancedSearchWithPagination(	
										String collection, 
										String queryField, 
										String searchTerm, 
										String startRecord, 
										String pageSize, 
										String tag, String order,
										String startPage);
}
