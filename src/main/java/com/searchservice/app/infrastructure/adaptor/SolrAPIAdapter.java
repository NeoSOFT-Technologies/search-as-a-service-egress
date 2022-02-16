package com.searchservice.app.infrastructure.adaptor;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.searchservice.app.domain.dto.SolrAPIAdapterResponse;
import com.searchservice.app.domain.port.spi.SolrAPIAdapterPort;
import com.searchservice.app.domain.utils.SolrAPIAdapterUtil;

@SuppressWarnings("deprecation")
@Service
public class SolrAPIAdapter implements SolrAPIAdapterPort {

	private final Logger log = LoggerFactory.getLogger(SolrAPIAdapter.class);

	@Override
	public SolrClient getSolrClient(String urlString, String tableName) {
		log.debug("Getting Solr Client for collection/table: {}", tableName);
		
		return new HttpSolrClient.Builder(urlString+"/"+tableName).build();
	}

	@Override
	public SolrClient getSolrCloudClient(String urlString, String tableName) {
		log.debug("Getting Solr Cloud Client for collection/table: {}", tableName);
		
		// Using already running Solr nodes
		return new CloudSolrClient.Builder().withSolrUrl(urlString).build();
	}
	
	@Override
	public CloudSolrClient getCloudSolrClient(String urlString, String tableName) {
		log.debug("Getting Cloud Solr Client for collection/table: {}", tableName);
		
		// Using already running Solr nodes
		return new CloudSolrClient.Builder().withSolrUrl(urlString).build();
	}

	@Override
	public SolrAPIAdapterResponse getSolrClientAdapter(String urlString, String tableName) {
		log.debug("Getting Solr Client for collection/table: {}", tableName);
		
		SolrAPIAdapterResponse responseDTO = new SolrAPIAdapterResponse();
			SolrClient solrClient = new HttpSolrClient.Builder(urlString+"/"+tableName).build();
			
			boolean solrClientFound = SolrAPIAdapterUtil.isSolrClientFound(solrClient);
			if(solrClientFound) {
				responseDTO.setStatusCode(200);
				responseDTO.setSolrClient(solrClient);
				log.debug("Solr client for collection: {} is fetched successfully", tableName);
				return responseDTO;
			} else {
				log.debug("Some error occured while fetching Solr Client for collection: {}", tableName);
				responseDTO.setStatusCode(404);
				responseDTO.setResponseMessage("Couldn't find Solr Client for given collection");
				return responseDTO;
			}
	}
 
}