package com.searchservice.app.infrastructure.adaptor;

import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.springframework.stereotype.Component;
import lombok.Data;


@Data
@Component
public class SolrSearchResult {
	private Long numDocs;
	private List<SolrDocument> data;

	@Override
	public String toString() {
		return "SolrSearchResult [numDocs=" + numDocs + ", solrDocuments=" + data + "]";
	}
}