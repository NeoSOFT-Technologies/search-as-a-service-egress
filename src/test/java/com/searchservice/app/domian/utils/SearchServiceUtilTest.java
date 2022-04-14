package com.searchservice.app.domian.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.utils.SearchServiceUtil;

 class SearchServiceUtilTest {
   
	HttpSolrClient searchServiceClient = new HttpSolrClient.Builder("http://localhost:8080/test/collection1").build();
	SolrClient client = new HttpSolrClient.Builder("http://localhost:8080/test/collection1").build();
	SolrClient clientEmpty;
	HttpSolrClient searchServiceClientEmpty;
	
	
	@Test
	 void closeSearchConnection()
	 {
		SearchServiceUtil.closeSearchServiceClientConnection(client);
		SearchServiceUtil.closeSearchServiceClientConnection(searchServiceClientEmpty);
		assertThat(client).isNotNull();
	 }
	
	@Test
	 void closeSearchConnectionWithEmptyClient()
	 {
		SearchServiceUtil.closeSearchServiceClientConnection(searchServiceClient);	
		SearchServiceUtil.closeSearchServiceClientConnection(clientEmpty);	
		assertThat(clientEmpty).isNull();
	 }
	

	
	
}
