package com.searchservice.app.domain.dto;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.infrastructure.adaptor.SearchResult;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponse {
	private int statusCode;
	private String message;
	private HttpStatus status;
	private SearchResult results;
	
	public SearchResponse(String responseMessage, SearchResult solrSearchResultResponse) {
		this.message = responseMessage;
		this.results = solrSearchResultResponse;
	}

	@Override
	public String toString() {
		return "SolrSearchResponseDTO [statusCode=" + statusCode + ", "
						+ "responseMessage=" + message + ", "
						+ "solrSearchResultResponse=" + results + "]";
	}

	public SearchResponse(int statusCode, String message) {
		super();
		this.statusCode = statusCode;
		this.message = message;
	}

	
}