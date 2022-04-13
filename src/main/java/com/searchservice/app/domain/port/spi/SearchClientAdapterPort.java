package com.searchservice.app.domain.port.spi;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.searchservice.app.domain.dto.SearchClientAdapterResponse;

public interface SearchClientAdapterPort {
	
	SolrClient getSearchClient(String urlString, String tableName);	
	SearchClientAdapterResponse getSearchClientAdapter(String urlString, String tableName);	
	QueryResponse getresponse(SolrClient client, SolrQuery query);
}