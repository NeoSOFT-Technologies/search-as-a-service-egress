package com.Searchutil.app;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.doThrow;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import com.searchservice.app.domain.utils.SearchServiceUtil;

public class SearchServiceUtilTest {
  
	HttpSolrClient searchServiceClient = new HttpSolrClient.Builder("http://localhost:8983/solr/collection1").build();
	SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/collection1").build();
	SolrClient clientEmpty;
	HttpSolrClient searchServiceClientEmpty;
	@Test
	 void closeSearchConnection()
	 {
		SearchServiceUtil.closeSearchServiceClientConnection(client);		
	 }
	
	@Test
	 void closeSearchConnection1()
	 {
		SearchServiceUtil.closeSearchServiceClientConnection(searchServiceClient);		
	 }
	

	@Test
	 void closeSearchConnectionExceptionHttp()
	 {
		SearchServiceUtil.closeSearchServiceClientConnection(searchServiceClientEmpty);		
	 }
	
	@Test
	 void closeSearchConnectionExceptionSolr()
	 {
		
		SearchServiceUtil.closeSearchServiceClientConnection(clientEmpty);		
	 }
	
	
}
