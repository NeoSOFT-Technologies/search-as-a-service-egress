package com.searchservice.app.infrastructure.adaptor;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import lombok.Data;


@Data
@Component
public class SolrSearchResult {
	private Long numDocs;
	private List<Map<String, Object>> data;

	@Override
	public String toString() {
		return "SolrSearchResult [numDocs=" + numDocs + ", solrDocuments=" + data + "]";
	}
}