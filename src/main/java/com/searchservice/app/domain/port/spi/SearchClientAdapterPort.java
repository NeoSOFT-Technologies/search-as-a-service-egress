package com.searchservice.app.domain.port.spi;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import com.searchservice.app.domain.dto.SearchClientAdapterResponse;

public interface SearchClientAdapterPort {
	
	SolrClient getSearchClient(String urlString, String tableName);
	SolrClient getSearchCloudClient(String urlString, String tableName);
	CloudSolrClient getCloudSearchClient(String urlString, String tableName);
	SearchClientAdapterResponse getSearchClientAdapter(String urlString, String tableName);
}