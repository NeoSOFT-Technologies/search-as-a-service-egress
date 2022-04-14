package com.searchservice.app.domain.dtos;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.dto.SearchClientAdapterResponse;

 class SearchClientAdapterResponseTest {
	SolrClient searchClient;
	SearchClientAdapterResponse res1=new SearchClientAdapterResponse();
	SearchClientAdapterResponse res2=new SearchClientAdapterResponse();
	
	List<String> data=new ArrayList<String>();
    
	@Test
	void testSearchClientAdapterResponse()
	{
		String message ="success";
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
		assertEquals(message, res1.getResponseMessage());
	}
	
	@Test 
	void testSearchClientAdapterResponse1()
	{
		int code= 200;
		res1.setStatusCode(200);
		res1.setSearchClient(searchClient);
		res1.setName("test");
		/* res1.s */
		res1.setData(data);
		assertEquals(code, res1.getStatusCode());
	}
}
