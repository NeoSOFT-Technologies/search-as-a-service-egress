package com.searchservice.app.domain.dtos;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.dto.SearchClientAdapterResponse;

public class SearchClientAdapterResponseTest {
	SolrClient searchClient;
	SearchClientAdapterResponse res1=new SearchClientAdapterResponse();
	SearchClientAdapterResponse res2=new SearchClientAdapterResponse();
	
	List<String> data=new ArrayList<String>();
    
	@Test
	void testSearchClientAdapterResponse()
	{
		res1.equals(res2);
		res1.hashCode();
		res1.toString();
		res1.getData();
		res1.getName();
		res1.setResponseMessage("success");
		res1.getSearchClient();
		res1.getResponseMessage();
		res1.getStatusCode();
		res1.getSearchClient();	
	}
	
	@Test 
	void testSearchClientAdapterResponse1()
	{
		res1.setStatusCode(200);
		res1.setSearchClient(searchClient);
		res1.setName("test");
		/* res1.s */
		res1.setData(data);
	}
}
