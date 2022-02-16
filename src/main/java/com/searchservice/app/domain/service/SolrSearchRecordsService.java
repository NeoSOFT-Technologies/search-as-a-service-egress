package com.searchservice.app.domain.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.searchservice.app.domain.dto.SolrSearchResponse;
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
	
	//@Autowired
	SolrSearchResult solrSearchResult = new SolrSearchResult();
	//@Autowired
	SolrSearchResponse solrSearchResponseDTO = new SolrSearchResponse();
	// call for solr client
	@Autowired
	SolrAPIAdapter solrSchemaAPIAdapter = new SolrAPIAdapter();
	
	@Value("${base-solr-url}")
	//	@Value("${base-solr-url}")
	String solrUrl;
	
	public SolrSearchRecordsService(
			SolrSearchResult solrSearchResult, 
			SolrSearchResponse solrSearchResponseDTO) {
		this.solrSearchResult = solrSearchResult;
		this.solrSearchResponseDTO = solrSearchResponseDTO;
	}

	@Override
	public SolrSearchResponse setUpSelectQueryUnfiltered(
											String collection) {
		/* Egress API -- solr collection records -- UNFILTERED SEARCH */
		logger.debug("Performing UNFILTERED solr search for given collection");
		
		SolrClient client = solrSchemaAPIAdapter.getSolrClient(solrUrl, collection);
		SolrQuery query = new SolrQuery();
		query.set("q", "*:*");
		solrSearchResponseDTO = new SolrSearchResponse();
		try {
			solrSearchResult = new SolrSearchResult();
			QueryResponse response = client.query(query);
			SolrDocumentList docs = response.getResults();
			List<Object> solrDocuments = new ArrayList<>();
			docs.forEach(solrDocuments::add);
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
	
	@Override
	public SolrSearchResponse setUpSelectQueryBasicSearch( 
														String collection, 
														String queryField, 
														String searchTerm) {
		/* Egress API -- solr collection records -- BASIC SEARCH (by QUERY FIELD) */
		logger.debug("Performing BASIC solr search for given collection");

		SolrClient client = solrSchemaAPIAdapter.getSolrClient(solrUrl, collection);
		SolrQuery query = new SolrQuery();
		query.set("q", queryField + ":" + searchTerm);
		solrSearchResponseDTO = new SolrSearchResponse();
		try {
			solrSearchResult = new SolrSearchResult();
			QueryResponse response = client.query(query);
			SolrDocumentList docs = response.getResults();
			
			List<Object> solrDocuments = new ArrayList<>();
			docs.forEach(solrDocuments::add);
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

	@Override
	public SolrSearchResponse setUpSelectQueryOrderedSearch(
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
		solrSearchResponseDTO = new SolrSearchResponse();
		try {
			solrSearchResult = new SolrSearchResult();
			QueryResponse response = client.query(query);
			SolrDocumentList docs = response.getResults();
			List<Object> solrDocuments = new ArrayList<>();
			docs.forEach(solrDocuments::add);
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
	
	@Override
	public SolrSearchResponse setUpSelectQueryAdvancedSearch(	
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
		solrSearchResponseDTO = new SolrSearchResponse();
		try {
			solrSearchResult = new SolrSearchResult();
			QueryResponse response = client.query(query);
			SolrDocumentList docs = response.getResults();
			List<Object> solrDocuments = new ArrayList<>();
			docs.forEach(solrDocuments::add);
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
	
	@Override
	public SolrSearchResponse setUpSelectQueryAdvancedSearchWithPagination(
															String collection, 
															String queryField, 
															String searchTerm,
															String startRecord, 
															String pageSize, 
															String tag, String order, 
															String startPage) {
		/* Egress API -- solr collection records -- PAGINATED SEARCH */
		logger.debug("Performing PAGINATED solr search for given collection");

		SolrClient client = solrSchemaAPIAdapter.getSolrClient(solrUrl, collection);
		SolrQuery query = new SolrQuery();
		query.set("q", queryField + ":" + searchTerm);
		query.set("start", startRecord);
		query.set("rows", pageSize);
		SortClause sortClause = new SortClause(tag, order);
		query.setSort(sortClause);
		solrSearchResponseDTO = new SolrSearchResponse();
		try {
			solrSearchResult = new SolrSearchResult();
			QueryResponse response = client.query(query);
			SolrDocumentList docs = response.getResults();
			List<Object> solrDocuments = new ArrayList<>();
			docs.forEach(solrDocuments::add);
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