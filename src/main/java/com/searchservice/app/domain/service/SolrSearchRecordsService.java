package com.searchservice.app.domain.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.SolrSearchResponseDTO;
import com.searchservice.app.domain.port.api.SolrSearchRecordsServicePort;
import com.searchservice.app.infrastructure.adaptor.SolrAPIAdapter;
import com.searchservice.app.infrastructure.adaptor.SolrSearchResult;


@Service
@Transactional
public class SolrSearchRecordsService implements SolrSearchRecordsServicePort {
	/*
	 * Solr Search Records for given collection- Egress Service
	 */  
	private final Logger logger = LoggerFactory.getLogger(SolrSearchRecordsService.class); 
	private static final String SUCCESS_MSG = "Records fetched successfully";
	private static final String FAILURE_MSG = "Records couldn't be fetched for given collection";
	private static final String SUCCESS_LOG = "Solr search operation is peformed successfully for given collection";
	private static final String FAILURE_LOG = "An exception occured while performing Solr Search Operation! ";
	
	SolrSearchResult solrSearchResult = new SolrSearchResult();
	SolrSearchResponseDTO solrSearchResponseDTO = new SolrSearchResponseDTO();
	@Autowired
	SolrAPIAdapter solrSchemaAPIAdapter = new SolrAPIAdapter();
	@Autowired
	TableService tableService;
	
	
	@Value("${base-solr-url}")
	String solrUrl;
	
	public SolrSearchRecordsService(
			SolrSearchResult solrSearchResult, 
			SolrSearchResponseDTO solrSearchResponseDTO) {
		this.solrSearchResult = solrSearchResult;
		this.solrSearchResponseDTO = solrSearchResponseDTO;
	}

	
	@Override
	public SolrSearchResponseDTO setUpSelectQueryUnfiltered(
											List<String> validSchemaColumns,
											String collection) {
		/* Egress API -- solr collection records -- UNFILTERED SEARCH */
		logger.debug("Performing UNFILTERED solr search for given collection");
		
		SolrClient client = solrSchemaAPIAdapter.getSolrClient(solrUrl, collection);
		SolrQuery query = new SolrQuery();
		query.set("q", "*:*");
		solrSearchResponseDTO = processSearchQuery(client, query, validSchemaColumns);
		
		return solrSearchResponseDTO;
	}
	
	@Override
	public SolrSearchResponseDTO setUpSelectQueryBasicSearch(
														List<String> validSchemaColumns,
														String collection, 
														String queryField, 
														String searchTerm) {
		/* Egress API -- solr collection records -- BASIC SEARCH (by QUERY FIELD) */
		logger.debug("Performing BASIC solr search for given collection");

		SolrClient client = solrSchemaAPIAdapter.getSolrClient(solrUrl, collection);
		SolrQuery query = new SolrQuery();
		query.set("q", queryField + ":" + searchTerm);
		solrSearchResponseDTO = processSearchQuery(client, query, validSchemaColumns);
		
		return solrSearchResponseDTO;
	}

	@Override
	public SolrSearchResponseDTO setUpSelectQueryOrderedSearch(
												List<String> validSchemaColumns, 
												String collection, 
												String queryField, 
												String searchTerm, 
												String tag, 
												String order) {
		/* Egress API -- solr collection records -- ORDERED SEARCH */
		logger.debug("Performing ORDERED solr search for given collection");

		SolrClient client = solrSchemaAPIAdapter.getSolrClient(solrUrl, collection);
		SolrQuery query = new SolrQuery();
		query.set("q", queryField + ":" + searchTerm);
		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		solrSearchResponseDTO = processSearchQuery(client, query, validSchemaColumns);
		
		return solrSearchResponseDTO;
	}
	
	
	@Override
	public SolrSearchResponseDTO setUpSelectQueryAdvancedSearch(
												List<String> validSchemaColumns, 
												String collection, 
												String queryField, 
												String searchTerm, 
												String startRecord, 
												String pageSize,
												String tag, 
												String order) {
		/* Egress API -- solr collection records -- ADVANCED SEARCH */
		logger.debug("Performing ADVANCED solr search for given collection");

		SolrClient client = solrSchemaAPIAdapter.getSolrClient(solrUrl, collection);
		SolrQuery query = new SolrQuery();
		query.set("q", queryField + ":" + searchTerm);
		query.set("start", startRecord);
		query.set("rows", pageSize);
		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		solrSearchResponseDTO = processSearchQuery(client, query, validSchemaColumns);
		
		return solrSearchResponseDTO;
	}
	

	@Override
	public SolrSearchResponseDTO setUpSelectQueryMultiFieldSearch(
												List<String> validSchemaColumns, 
												String collection, 
												String queryField, // expected comma separated column names
												String searchTerm, // expected comma separated column values
												String startRecord, 
												String pageSize,
												String tag, 
												String order) {
		/* Egress API -- solr collection records -- ADVANCED SEARCH */
		logger.debug("Performing ADVANCED solr search for given collection");

		SolrClient client = solrSchemaAPIAdapter.getSolrClient(solrUrl, collection);
		SolrQuery query = new SolrQuery();
		query.set("q", queryField + ":" + searchTerm);
		query.set("start", startRecord);
		query.set("rows", pageSize);
		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		solrSearchResponseDTO = processSearchQuery(client, query, validSchemaColumns);
		
		return solrSearchResponseDTO;
	}
	
	
	public SolrSearchResponseDTO processSearchQuery(SolrClient client, SolrQuery query, List<String> validSchemaColumns) {
		try {
			solrSearchResult = new SolrSearchResult();
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
		}
		return solrSearchResponseDTO;
	}
	
}