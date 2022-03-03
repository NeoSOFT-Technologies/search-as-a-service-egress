package com.searchservice.app.domain.port.api;

import java.util.List;

import org.json.JSONArray;

import com.searchservice.app.domain.dto.SearchResponse;

public interface SearchServicePort {
	SearchResponse setUpSelectQueryUnfiltered(
			List<String> validSchemaColumns, 
			String collection);

	SearchResponse setUpSelectQueryBasicSearch(
			List<String> validSchemaColumns, 
			String collection, 
			String queryField, 
			String searchTerm);

	SearchResponse setUpSelectQueryOrderedSearch(
			List<String> validSchemaColumns, 
			String collection, 
			String queryField, 
			String searchTerm, 
			String tag, 
			String order);
	
	SearchResponse setUpSelectQuery(
			List<String> validSchemaColumns, 
			JSONArray currentTableSchema, 
			String collection, 
			String queryField, 
			String searchTerm, 
			String searchOperator, 
			String startRecord, 
			String pageSize, 
			String tag, 
			String order);
	
	SearchResponse setUpSelectQueryAdvancedSearch(
			List<String> validSchemaColumns, 
			String collection, 
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
