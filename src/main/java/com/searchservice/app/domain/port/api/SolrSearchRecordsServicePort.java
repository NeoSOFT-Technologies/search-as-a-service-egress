package com.searchservice.app.domain.port.api;

import java.util.List;

import org.json.JSONArray;

import com.searchservice.app.domain.dto.SolrSearchResponseDTO;

public interface SolrSearchRecordsServicePort {
	SolrSearchResponseDTO setUpSelectQueryUnfiltered(
			List<String> validSchemaColumns, 
			String collection);

	SolrSearchResponseDTO setUpSelectQueryBasicSearch(
			List<String> validSchemaColumns, 
			String collection, 
			String queryField, 
			String searchTerm);

	SolrSearchResponseDTO setUpSelectQueryOrderedSearch(
			List<String> validSchemaColumns, 
			String collection, 
			String queryField, 
			String searchTerm, 
			String tag, 
			String order);
	
	SolrSearchResponseDTO setUpSelectQueryMultifieldSearch(
			List<String> validSchemaColumns, 
			String collection, 
			String queryField, 
			String searchTerm, 
			String startRecord, 
			String pageSize, 
			String tag, 
			String order);
	
	SolrSearchResponseDTO setUpSelectQueryMultifieldAndMultivalueSearch(
			List<String> validSchemaColumns, 
			JSONArray currentTableSchema, 
			String collection, 
			String queryField, 
			String searchTerm, 
			String startRecord, 
			String pageSize, 
			String tag, 
			String order);
	
	SolrSearchResponseDTO setUpSelectQuery(
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
