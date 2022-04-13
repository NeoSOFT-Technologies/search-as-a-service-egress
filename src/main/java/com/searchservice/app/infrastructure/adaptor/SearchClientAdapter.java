package com.searchservice.app.infrastructure.adaptor;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.searchservice.app.domain.dto.SearchClientAdapterResponse;
import com.searchservice.app.domain.port.spi.SearchClientAdapterPort;
import com.searchservice.app.domain.utils.SearchClientAdapterUtil;


@SuppressWarnings("deprecation")
@Service
public class SearchClientAdapter implements SearchClientAdapterPort {

	private final Logger log = LoggerFactory.getLogger(SearchClientAdapter.class);

	@Override
	public SolrClient getSearchClient(String urlString, String tableName) {
		log.debug("Getting Search Client for collection/table: {}", tableName);
		
		return new HttpSolrClient.Builder(urlString+"/"+tableName).build();
	}

	

	@Override
	public SearchClientAdapterResponse getSearchClientAdapter(String urlString, String tableName) {
		log.debug("Getting Search Client for collection/table: {}", tableName);
		
		SearchClientAdapterResponse responseDTO = new SearchClientAdapterResponse();
			SolrClient searchClient = new HttpSolrClient.Builder(urlString+"/"+tableName).build();
			
			boolean searchClientFound = SearchClientAdapterUtil.isSearchClientFound(searchClient);
			if(searchClientFound) {
				responseDTO.setStatusCode(200);
				responseDTO.setSearchClient(searchClient);
				log.debug("Search client for collection: {} is fetched successfully", tableName);
				return responseDTO;
			} else {
				log.debug("Some error occured while fetching Search Client for collection: {}", tableName);
				responseDTO.setStatusCode(404);
				responseDTO.setResponseMessage("Couldn't find Search Client for given collection");
				return responseDTO;
			}
	}

	@Override
	public QueryResponse getresponse(SolrClient client, SolrQuery query) {
		QueryResponse res = new QueryResponse();
		try {
			 res =	 client.query(query);
		} catch (SolrServerException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}
 
}