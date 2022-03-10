package com.searchservice.app.domain.utils;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;

public class SearchClientAdapterUtil {
	
	private SearchClientAdapterUtil() {}
	
	public static boolean isSearchClientFound(SolrClient searchClient) {
		try {
			SolrQuery query = new SolrQuery();
			query.set("q", "*:*");
			searchClient.query(query);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
}