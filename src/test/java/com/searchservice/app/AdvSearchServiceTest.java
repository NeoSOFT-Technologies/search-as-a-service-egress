package com.searchservice.app;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BaseCloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.domain.dto.logger.Loggers;
import com.searchservice.app.domain.port.api.SearchServicePort;
import com.searchservice.app.domain.port.spi.SearchClientAdapterPort;
import com.searchservice.app.domain.service.AdvSearchService;
import com.searchservice.app.domain.service.SearchService;
import com.searchservice.app.domain.service.TableService;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.infrastructure.adaptor.SearchClientAdapter;
import com.searchservice.app.infrastructure.adaptor.SearchResult;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(properties = { "test-solr-url: http://localhost:8983/solr", "test-solr-collection: techproducts" })
class AdvSearchServiceTest {
	private final Logger logger = LoggerFactory.getLogger(AdvSearchServiceTest.class);

	@Value("${test-solr-collection}")
	private String SOLR_COLLECTION;
	@Value("${test-solr-url}")
	private String SOLR_URL;
	List<String> validSchemaColumns = new ArrayList<String>(Arrays.asList("id", "category"));
	JSONArray currentTableSchema;

	/* Mock the dependencies */
	@MockBean
	private SearchServicePort solrSearchRecordsServicePort;
	
	@MockBean
	TableService tableService;
	
	@MockBean
	private SearchClientAdapter solrAPIAdapterMock;

	@MockBean
	SolrClient solrclient;

	SearchResult searchResult = new SearchResult();

	@InjectMocks
	private AdvSearchService solrSearchRecordsService;
	
	String query = "q=*&start=0&rows=5&sort=id+asc";

	
String json = "{\r\n"
		+ "\"books\" :[\r\n"
		+ "  {\r\n"
		+ "    \"id\" : 1,\r\n"
		+ "    \"name\" : \"queryField\",\r\n"
		+ "    \"author\" : \"Rick Riordan\",\r\n"
		+ "    \"multiValue\" : true\r\n"
		+ "  }  \r\n"
		+ "]\r\n"
		+ "}";

	JSONObject jobj = new JSONObject(json);
	JSONArray jarray = jobj.getJSONArray("books");

	@Mock
	SolrClient solrClient;

	Loggers loggersDTO = new Loggers();
	Map<String, Object> id = new HashMap<String, Object>() {
		private static final long serialVersionUID = 1L;

		{
			put("name", "id");
			put("type", "string");
			put("multiValued", "false");
			put("uninvertible", "true");
			put("indexed", "true");
			put("stored", "true");
		}
	};

	@SuppressWarnings("deprecation")
	@BeforeEach
	void setUp() throws SolrServerException, IOException {
		MockitoAnnotations.initMocks(this);
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod("nameofCurrMethod");
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename("servicename");
		loggersDTO.setUsername("username");	
		
		when(solrAPIAdapterMock.getSearchClient(Mockito.any(), Mockito.any())).thenReturn(solrClient);

		List<String> categoryList = Arrays.asList("Christopher Osborne", "Melanie Calderon", "Jessica Jacobs");
		List<Number> vendorIdList = Arrays.asList(56, 50.73);
		SolrDocument solrDocs = new SolrDocument();
		solrDocs.setField("id", 0);
		solrDocs.setField("title", "Michelle Giles");
		solrDocs.setField("product_name", "Amazing title - Michelle Giles");
		solrDocs.setField("category", categoryList);
		solrDocs.setField("price", Long.valueOf(3500));
		solrDocs.setField("vendor_id", vendorIdList);
		solrDocs.setField("is_available", true);
		solrDocs.setField("_version_", 1728887091072335872L);
		SolrDocumentList list = new SolrDocumentList();
		list.add(solrDocs);
		list.setNumFound(1);
		
		when(solrAPIAdapterMock.getSearchClient(Mockito.any(), Mockito.any())).thenReturn(solrClient);
		QueryResponse emptyResponse = new QueryResponse();
		emptyResponse.setResponse(new NamedList<>(Map.of("response", list)));
		List<Map<String, Object>> listmap = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", "7");
		map.put("title", "the monk");
		map.put("category", "shubham");
		listmap.add(map);

		Mockito.when(solrAPIAdapterMock.getQueryResponse(Mockito.any(), Mockito.any())).thenReturn(emptyResponse);
		Mockito.when(tableService.getValidDocumentsList(Mockito.any(), Mockito.any())).thenReturn(listmap);			
	}

	
	@Test
	void testSetUpSelectQueryearch() {
		logger.info("Solr Search ADVANCED service test is started..");
		int expectedStatusResponse = 200;
		SearchResponse receivedResponse;
		receivedResponse = solrSearchRecordsService.setUpSelectQuerySearchViaQuery(validSchemaColumns, SOLR_COLLECTION,
				"*", "0", "5", "id", "asc");
		assertEquals(expectedStatusResponse, receivedResponse.getStatusCode());
		logger.info("Positive testing is completed.");

	}

	
	@Test
	  
	  void testSetUpSelectQueryfieldSearch() {
	  logger.info("Solr Search ADVANCED service test is started..");
	  
	  int expectedStatusResponse = 200; 
	  SearchResponse receivedResponse = null;
	  
	  
	  receivedResponse = solrSearchRecordsService.setUpSelectQuerySearchViaQueryField(
	  validSchemaColumns, jarray, SOLR_COLLECTION, "*","*", "0", "5", "id", "asc");
	  
	  assertEquals( expectedStatusResponse, receivedResponse.getStatusCode());
	  logger.info("Positive testing is completed.");
	}
}
