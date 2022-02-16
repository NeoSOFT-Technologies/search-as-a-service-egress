package com.searchservice.app.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.searchservice.app.domain.dto.SolrSearchResponse;
import com.searchservice.app.domain.service.SolrSearchAdvanced;
import com.searchservice.app.infrastructure.adaptor.SolrSearchResult;

@RestController
@RequestMapping("/search/api")
public class SearchResource {
    /* Solr Search Records for given collection- Egress Service Resource */
    private final Logger logger = LoggerFactory.getLogger(SearchResource.class);

    private SolrSearchAdvanced solrSearchAdvanced;

    public SearchResource(

            SolrSearchAdvanced solrSearchAdvanced) {
        this.solrSearchAdvanced = solrSearchAdvanced;

    }

    @Autowired
    SolrSearchResult solrSearchResult;

    @GetMapping(value = "/v1/{clientId}/{tableName}")
    public ResponseEntity<SolrSearchResponse> searchRecordsInGivenCollectionAdvanced(@PathVariable int clientId, @PathVariable String tableName,
            @RequestParam(defaultValue = "name") String queryField, @RequestParam(defaultValue = "*") String searchTerm, @RequestParam(defaultValue = "0") String startRecord,
            @RequestParam(defaultValue = "5") String pageSize, @RequestParam(defaultValue = "id") String orderBy, @RequestParam(defaultValue = "asc") String order) {
        logger.debug("REST call for ADVANCED SEARCH search in the given collection");
        tableName = tableName + "_" + clientId;
        SolrSearchResponse solrSearchResponseDTO = solrSearchAdvanced.search(tableName, queryField, searchTerm, startRecord, pageSize, orderBy, order);
        if (solrSearchResponseDTO.getStatusCode() == 200) {
            return ResponseEntity.status(HttpStatus.OK).body(solrSearchResponseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(solrSearchResponseDTO);
        }
    }

}