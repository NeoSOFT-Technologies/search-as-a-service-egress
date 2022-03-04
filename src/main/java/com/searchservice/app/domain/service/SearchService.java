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
import com.searchservice.app.infrastructure.adaptor.SolrClientAdapter;
import com.searchservice.app.infrastructure.adaptor.SearchResult;
import com.searchservice.app.rest.errors.OperationNotAllowedException;


@Service
@Transactional
public class SearchService implements SearchServicePort {
	private static final String PAGE_SIZE = "rows";
	private static final String SEARCH_QUERY = "q";
	private static final String START_PAGE = "start";
	/*
	 * Solr Search Records for given collection- Egress Service
	 */  
	private final Logger logger = LoggerFactory.getLogger(SearchService.class); 
	private static final String SUCCESS_MSG = "Records fetched successfully";
	private static final String FAILURE_MSG = "Records couldn't be fetched for given collection";
	private static final String SUCCESS_LOG = "Solr search operation is peformed successfully for given collection";
	private static final String FAILURE_LOG = "An exception occured while performing Solr Search Operation! ";
	
	SearchResult solrSearchResult = new SearchResult();
	SearchResponse solrSearchResponseDTO = new SearchResponse();
	@Autowired
	SolrClientAdapter solrSchemaAPIAdapter = new SolrClientAdapter();
	@Autowired
	TableService tableService;
	
	
	@Value("${base-solr-url}")
	String solrUrl;
	
	public SearchService(
			SearchResult solrSearchResult, 
			SearchResponse solrSearchResponseDTO) {
		this.solrSearchResult = solrSearchResult;
		this.solrSearchResponseDTO = solrSearchResponseDTO;
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

		SolrClient client = solrSchemaAPIAdapter.getSolrClient(solrUrl, tableName);
		SolrQuery query = new SolrQuery();
		
		// VALIDATE queryField
		boolean isQueryFieldValidated = SearchUtil.checkIfNameIsAlphaNumeric(queryField.trim());
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
		solrSearchResponseDTO = processSearchQuery(client, query, validSchemaColumns);
		
		return solrSearchResponseDTO;
	}
	

	@Override
	public SearchResponse setUpSelectQuerySearchViaQuery(
			List<String> validSchemaColumns,
			String tableName, 
			String searchQuery, 
			String startRecord, String pageSize, String tag, String order) {
		/* Egress API -- table records -- SEARCH VIA QUERY */
		logger.debug("Performing Search VIA QUERY for given collection");

		SolrClient client = solrSchemaAPIAdapter.getSolrClient(solrUrl, tableName);
		
		SolrQuery query = new SolrQuery();
		query.set(SEARCH_QUERY, searchQuery);
		query.set(START_PAGE, startRecord);
		query.set(PAGE_SIZE, pageSize);
		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		solrSearchResponseDTO = processSearchQuery(client, query, validSchemaColumns);
		
		return solrSearchResponseDTO;
	}
	
	
	// Auxiliary methods
	public SearchResponse processSearchQuery(SolrClient client, SolrQuery query, List<String> validSchemaColumns) {
		try {
			solrSearchResult = new SearchResult();
			QueryResponse response = client.query(query);
			
			SolrDocumentList docs = response.getResults();

			List<Map<String, Object>> solrDocuments = new ArrayList<>();
			// Sync Table documents with soft deleted schema; add valid documents
			if(validSchemaColumns.isEmpty())
				docs.forEach(solrDocuments::add);
			else
				solrDocuments = tableService.getValidDocumentsList(
					docs, validSchemaColumns);

			response = client.query(query);
			
			response.getDebugMap();
			long numDocs = docs.getNumFound();
			solrSearchResult.setNumDocs(numDocs);
			solrSearchResult.setData(solrDocuments);
			// Prepare SolrSearchResponse
			solrSearchResponseDTO.setStatusCode(200);
			solrSearchResponseDTO.setResponseMessage(SUCCESS_MSG);
			solrSearchResponseDTO.setResults(solrSearchResult);
			logger.debug(SUCCESS_LOG);
			return solrSearchResponseDTO;
		} catch (SolrServerException | IOException | NullPointerException e) {
			solrSearchResponseDTO.setStatusCode(400);
			solrSearchResponseDTO.setResponseMessage(FAILURE_MSG);
			logger.error(FAILURE_LOG, e);
		} catch(Exception e) {
			solrSearchResponseDTO.setStatusCode(400);
			if(e.getMessage().contains("Cannot parse")) {				
				solrSearchResponseDTO.setResponseMessage("Couldn't parse the search query. Please provide query in correct format");
			} else
				solrSearchResponseDTO.setResponseMessage(FAILURE_MSG);
		}
		
		return solrSearchResponseDTO;
	}
	
}