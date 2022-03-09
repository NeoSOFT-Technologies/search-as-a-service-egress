package com.searchservice.app.domain.port.api;

import java.util.List;

import org.json.JSONArray;

import com.searchservice.app.domain.dto.SearchResponse;

public interface SearchServicePort {	
	SearchResponse setUpSelectQuerySearchViaQueryField(
			List<String> validSchemaColumns, 
			JSONArray currentTableSchema, 
			String tableName, 
			String queryField, 
			String searchTerm, 
			String startRecord, 
			String pageSize, 
			String tag, 
			String order);
	
	SearchResponse setUpSelectQuerySearchViaQuery(
			List<String> validSchemaColumns, 
			String collection, 
			String searchQuery, 
			String startRecord, 
			String pageSize, 
			String tag, 
			String order);
}
