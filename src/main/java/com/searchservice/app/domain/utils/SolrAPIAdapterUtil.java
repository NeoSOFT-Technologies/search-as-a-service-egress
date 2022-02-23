package com.searchservice.app.domain.utils;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;

public class SolrAPIAdapterUtil {
	
	private SolrAPIAdapterUtil() {}
	
	public static boolean isSolrClientFound(SolrClient solrClient) {
		try {
			SolrQuery query = new SolrQuery();
			query.set("q", "*:*");
			solrClient.query(query);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
}