package com.searchservice.app.domain.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.domain.port.api.SearchServicePort;
import com.searchservice.app.domain.utils.SearchUtil;
import com.searchservice.app.infrastructure.adaptor.SearchClientAdapter;
import com.searchservice.app.infrastructure.adaptor.SearchResult;
import com.searchservice.app.rest.errors.OperationNotAllowedException;


@Service
@Transactional
public class SearchService implements SearchServicePort {
	private static final String PAGE_SIZE = "rows";
	private static final String SEARCH_QUERY = "q";
	private static final String START_PAGE = "start";
	/*
	 * Search Records for given collection- Egress Service
	 */  
	private final Logger logger = LoggerFactory.getLogger(SearchService.class); 
	private static final String SUCCESS_MSG = "Records fetched successfully";
	private static final String FAILURE_MSG = "Records couldn't be fetched for given collection";
	private static final String SUCCESS_LOG = "Server search operation is peformed successfully for given collection";
	private static final String FAILURE_LOG = "An exception occured while performing Server Search Operation! ";
	
	SearchResult searchResult = new SearchResult();
	SearchResponse searchResponseDTO = new SearchResponse();
	@Autowired
	SearchClientAdapter searchSchemaAPIAdapter = new SearchClientAdapter();
	@Autowired
	TableService tableService;
	
	
	@Value("${base-search-url}")
	String searchUrl;
	
	public SearchService(
			SearchResult searchResult, 
			SearchResponse searchResponseDTO) {
		this.searchResult = searchResult;
		this.searchResponseDTO = searchResponseDTO;
	}
	
	
	@Override
	public SearchResponse setUpSelectQuerySearchViaQueryField(
												List<String> validSchemaColumns, 
												JSONArray currentTableSchema, 
												String tableName, 
												String queryField, // expected single column name
												String searchTerm, // expected search term for given column
												String startRecord, 
												String pageSize,
												String tag, 
												String order) {
		/* Egress API -- table records -- SEARCH via query-field */
		logger.debug("Performing records-search via query field & search term provided for given table");

		SolrClient client = searchSchemaAPIAdapter.getSearchClient(searchUrl, tableName);
		SolrQuery query = new SolrQuery();
		
		// VALIDATE queryField
		boolean isQueryFieldValidated = SearchUtil.checkIfNameIsAlphaNumeric(queryField.trim()) || queryField.trim().equals("*");
		if(!isQueryFieldValidated)
			throw new OperationNotAllowedException(
					406, 
					"Query-field validation unsuccessful. Query-field entry can only be in alphanumeric format");
		// VALIDATE queryField & searchTerm
		boolean isQueryFieldMultivalued = SearchUtil.isQueryFieldMultivalued(
				queryField, 
				currentTableSchema);
		
		// Set up query
		StringBuilder queryString = new StringBuilder();
		if(!isQueryFieldMultivalued) {			
			queryString.append(queryField + ":" + searchTerm);
		} else {
			List<String> searchTerms = SearchUtil.getTrimmedListOfStrings(Arrays.asList(searchTerm.split(",")));
			SearchUtil.setQueryForMultivaluedField(queryField, searchTerms, queryString);
		}
		
		query.set(SEARCH_QUERY, queryString.toString());
		query.set(START_PAGE, startRecord);
		query.set(PAGE_SIZE, pageSize);
		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		searchResponseDTO = processSearchQuery(client, query, validSchemaColumns);
		
		return searchResponseDTO;
	}
	

	@Override
	public SearchResponse setUpSelectQuerySearchViaQuery(
			List<String> validSchemaColumns,
			String tableName, 
			String searchQuery, 
			String startRecord, String pageSize, String tag, String order) {
		/* Egress API -- table records -- SEARCH VIA QUERY */
		logger.debug("Performing Search VIA QUERY for given collection");

		SolrClient client = searchSchemaAPIAdapter.getSearchClient(searchUrl, tableName);
		
		SolrQuery query = new SolrQuery();
		query.set(SEARCH_QUERY, searchQuery);
		query.set(START_PAGE, startRecord);
		query.set(PAGE_SIZE, pageSize);
		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		searchResponseDTO = processSearchQuery(client, query, validSchemaColumns);
		
		return searchResponseDTO;
	}
	
	
	// Auxiliary methods
	public SearchResponse processSearchQuery(SolrClient client, SolrQuery query, List<String> validSchemaColumns) {
		try {
			searchResult = new SearchResult();
			QueryResponse response = client.query(query);
			
			SolrDocumentList docs = response.getResults();

			List<Map<String, Object>> searchDocuments = new ArrayList<>();
			// Sync Table documents with soft deleted schema; add valid documents
			if(validSchemaColumns.isEmpty())
				docs.forEach(searchDocuments::add);
			else
				searchDocuments = tableService.getValidDocumentsList(
					docs, validSchemaColumns);

			response = client.query(query);
			
			response.getDebugMap();
			long numDocs = docs.getNumFound();
			searchResult.setNumDocs(numDocs);
			searchResult.setData(searchDocuments);
			// Prepare SearchResponse
			searchResponseDTO.setStatusCode(200);
			searchResponseDTO.setResponseMessage(SUCCESS_MSG);
			searchResponseDTO.setResults(searchResult);
			logger.debug(SUCCESS_LOG);
			return searchResponseDTO;
		} catch (SolrServerException | IOException | NullPointerException e) {
			searchResponseDTO.setStatusCode(400);
			searchResponseDTO.setResponseMessage(FAILURE_MSG);
			logger.error(FAILURE_LOG, e);
		} catch(Exception e) {
			searchResponseDTO.setStatusCode(400);
			if(e.getMessage().contains("Cannot parse")) {				
				searchResponseDTO.setResponseMessage("Couldn't parse the search query. Please provide query in correct format");
			} else
				searchResponseDTO.setResponseMessage(FAILURE_MSG);
		}
		
		return searchResponseDTO;
	}
	
}