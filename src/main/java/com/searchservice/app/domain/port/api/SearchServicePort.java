package com.searchservice.app.domain.port.api;

import java.util.List;

import org.json.JSONArray;

import com.searchservice.app.domain.dto.SearchResponseDTO;

public interface SearchServicePort {
	SearchResponseDTO setUpSelectQueryUnfiltered(
			List<String> validSchemaColumns, 
			String collection);

	SearchResponseDTO setUpSelectQueryBasicSearch(
			List<String> validSchemaColumns, 
			String collection, 
			String queryField, 
			String searchTerm);

	SearchResponseDTO setUpSelectQueryOrderedSearch(
			List<String> validSchemaColumns, 
			String collection, 
			String queryField, 
			String searchTerm, 
			String tag, 
			String order);
	
	SearchResponseDTO setUpSelectQuery(
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
	
	SearchResponseDTO setUpSelectQueryAdvancedSearch(
			List<String> validSchemaColumns, 
			String collection, 
			String queryField, 
			String searchTerm,
			String startRecord, 
			String pageSize, 
			String tag, 
			String order);
	
	SearchResponseDTO setUpSelectQuerySearchViaQuery(
			List<String> validSchemaColumns, 
			String collection, 
			String searchQuery, 
			String startRecord, 
			String pageSize, 
			String tag, 
			String order);
}
