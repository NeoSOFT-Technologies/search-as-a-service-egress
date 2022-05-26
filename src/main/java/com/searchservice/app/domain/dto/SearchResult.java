package com.searchservice.app.domain.dto;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;


@Data
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResult {
	private Long numDocs;
	private List<Map<String, Object>> data;

	@Override
	public String toString() {
		return "SolrSearchResult [numDocs=" + numDocs + ", solrDocuments=" + data + "]";
	}
}