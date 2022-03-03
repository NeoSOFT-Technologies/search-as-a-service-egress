package com.searchservice.app.domain.dto;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.searchservice.app.infrastructure.adaptor.SolrSearchResult;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponseDTO {
	private int statusCode;
	private String responseMessage;
	private SolrSearchResult results;
	
	public SearchResponseDTO(String responseMessage, SolrSearchResult solrSearchResultResponse) {
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