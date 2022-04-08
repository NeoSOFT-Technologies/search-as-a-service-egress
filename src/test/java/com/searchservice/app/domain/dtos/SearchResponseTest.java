package com.searchservice.app.domain.dtos;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.infrastructure.adaptor.SearchResult;

public class SearchResponseTest {

	HttpStatus status=new HttpStatus(); 
	 SearchResult solrSearchResultResponse=new  SearchResult();
	 SearchResponse s1=new  SearchResponse();
	 SearchResponse s2=new  SearchResponse();
	 
	 SearchResponse s3=new  SearchResponse(400,"fail");
	 SearchResponse s4=new  SearchResponse(400,"failure");
	 
	 SearchResponse s5=new SearchResponse("fail",solrSearchResultResponse);
	 SearchResponse s6=new SearchResponse("failure",solrSearchResultResponse);
	 @Test 
	 void testSearchResponse()
	 {
		 s1.equals(s2);
		 s1.getStatus();
		 s1.getResults();
		 s1.setStatusCode(200);
		 s1.setMessage("success");
		 s1.toString();
		 s1.hashCode();
	 }
	 
	@Test
	void testSearchResponse1() {
		s3.equals(s4);
		s3.getStatus();
		s3.getResults();
		s3.getMessage();
		s3.setStatusCode(200);
		s3.setMessage("success");
		s3.toString();
		s3.hashCode();
	}
	
	@Test 
	void testSearchResponse2()
	{
		s5.equals(s6);
		s5.getStatus();
		s5.getResults();
		s5.getMessage();
		s5.setStatusCode(200);
		s5.setMessage("Success");
		s5.toString();
		s5.hashCode();
		//s5.setStatus(status);
	}
}
