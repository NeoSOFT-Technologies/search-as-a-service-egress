package com.searchservice.app.domain.dto;

import org.springframework.stereotype.Component;

import com.searchservice.app.infrastructure.adaptor.SolrSearchResult;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
public class SolrSearchResponse {
	private int statusCode;
	private String responseMessage;
	private SolrSearchResult results;
	
	public SolrSearchResponse(String responseMessage, SolrSearchResult solrSearchResultResponse) {
		this.responseMessage = responseMessage;
		this.results = solrSearchResultResponse;
	}

	@Override
	public String toString() {
		return "SolrSearchResponseDTO [statusCode=" + statusCode + ", "
						+ "responseMessage=" + responseMessage + ", "
						+ "solrSearchResultResponse=" + results + "]";
	}
}