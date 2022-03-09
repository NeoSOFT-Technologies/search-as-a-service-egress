package com.searchservice.app.domain.dto;

import java.util.List;

import org.apache.solr.client.solrj.SolrClient;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SolrClientAdapterResponse {
	private int statusCode;
	private String responseMessage;
	private SolrClient solrClient;
    private String name;
    private List<String> data;
	@Override
	public String toString() {
		return "SolrAPIAdapterResponseDTO [statusCode=" + statusCode + ", responseMessage=" + responseMessage
				+ ", solrClient=" + solrClient + "]";
	}
}