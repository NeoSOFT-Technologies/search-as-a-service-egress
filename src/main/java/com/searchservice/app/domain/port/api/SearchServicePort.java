package com.searchservice.app.domain.port.api;

import java.util.List;

import org.json.JSONArray;

import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.domain.dto.logger.Loggers;

public interface SearchServicePort {	
	SearchResponse searchQuery(
			int clientId, String tableName, 
			String searchQuery, 
			String startRecord, String pageSize, String sortTag, String sortOrder, Loggers loggersDTO);
	
	SearchResponse searchField(int clientId, String tableName, String queryField, String queryFieldSearchTerm,
			String startRecord, String pageSize, String sortTag, String sortOrder, Loggers loggersDTO);
	
	
}
