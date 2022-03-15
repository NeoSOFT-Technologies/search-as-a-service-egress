package com.searchservice.app.domain.utils;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class SearchServiceUtil {
	
	private SearchServiceUtil() {}
	
	public static void closeSearchServiceClientConnection(HttpSolrClient searchServiceClient) {
		try {
			searchServiceClient.close();
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
	}
	
	public static void closeSearchServiceClientConnection(SolrClient searchServiceClient) {
		try {
			searchServiceClient.close();
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
	}
}
