package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.SearchResponse;


public interface SearchServicePort {	
	SearchResponse searchQuery(
			int clientId, String tableName, 
			String searchQuery, 
			String startRecord, String pageSize, String sortTag, String sortOrder);
	
	SearchResponse searchField(int clientId, String tableName, String queryField, String queryFieldSearchTerm,
			String startRecord, String pageSize, String sortTag, String sortOrder);
	
	
}
