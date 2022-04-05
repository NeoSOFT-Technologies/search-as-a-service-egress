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
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.infrastructure.adaptor.SearchClientAdapter;


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
	// @Autowired
	@MockBean
	private SearchClientAdapter solrAPIAdapterMock;
	
	@MockBean
	SolrClient solrclient;

	@InjectMocks
	private AdvSearchService solrSearchRecordsService;
	
	//SolrClient client= "{responseHeader={zkConnected=true,status=0,QTime=3,params={q=category: shubham AND category: karthik,start=0,sort=id asc,rows=5,wt=javabin,version=2}},response={numFound=1,numFoundExact=true,start=0,docs=[SolrDocument{id=7, title=the aaaaaaaaaa, category=[shubham, mangesh, karthik, abc], _version_=1726096559946334208}]}}";

    Map<String, Map<String, Object>> getSchemaKeyValuePair(){
        Map<String, Map<String, Object>> schemaKeyValuePair = new HashMap<>();
        Map<String, Object> id=new HashMap<String, Object>(){{
            put("name" , "id");
            put("type" , "string");
            put("multiValued", "false");
            put("uninvertible" , "true");
            put("indexed" , "true");
            put("stored" , "true");
        }};
        schemaKeyValuePair.put("id",id);
		return schemaKeyValuePair;
        
    }
	
	//QueryResponse clintjson= new QueryResponse(client);
	
	String query = "q=*&start=0&rows=5&sort=id+asc";

	private SearchClientAdapter solrAPIAdapter = new SearchClientAdapter();
	
	@Mock
	SolrClient solrClient;

	/*
	 * void setUpMockitoForInvalidCollection(String invalidCollection) {
	 * SolrClientAdapterResponse solrApiResponseDTO = solrAPIAdapter
	 * .getSolrClientAdapter(SOLR_URL, invalidCollection);
	 * 
	 * int responseStatusCode = solrApiResponseDTO.getStatusCode();
	 * if(responseStatusCode == 200) { solrClient =
	 * solrApiResponseDTO.getSolrClient();
	 * when(solrAPIAdapterMock.getSolrClient(Mockito.any(), Mockito.any()))
	 * .thenReturn(solrClient); } else {
	 * when(solrAPIAdapterMock.getSolrClient(Mockito.any(), Mockito.any()))
	 * .thenReturn(null); } }
	 */
	 Loggers loggersDTO =new Loggers() ;
	 private  SearchResponse responseDTO = new SearchResponse();
	 
	 Map<String, Object> id=new HashMap<String, Object>(){{
         put("name" , "id");
         put("type" , "string");
         put("multiValued", "false");
         put("uninvertible" , "true");
         put("indexed" , "true");
         put("stored" , "true");
     }};
    private BaseCloudSolrClient baseCloudSolrClient;
	@SuppressWarnings("deprecation")
	@BeforeEach
	void setUp() throws SolrServerException, IOException {
		MockitoAnnotations.initMocks(this);
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod("nameofCurrMethod");
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename("servicename");
		loggersDTO.setUsername("username");		
    
		solrClient = solrAPIAdapter.getSearchCloudClient(SOLR_URL, SOLR_COLLECTION);
		System.out.println("zzzzzzzzzzzzz     "+solrClient);
		when(solrAPIAdapterMock.getSearchClient(Mockito.any(), Mockito.any())).thenReturn(solrClient);
		QueryResponse emptyResponse = new QueryResponse();
		emptyResponse.setResponse(new NamedList<>(Map.of("response", new SolrDocumentList())));
		QueryResponse response = createAndInitEmptySolrQueryReponseMock();
		System.out.println("aaaaaaaaacvcv  "+ response);
	
		//when(solrClient.query( Mockito.any(),Mockito.any(SolrQuery.class)));
		
		Mockito.when(solrAPIAdapterMock.getresponse(Mockito.any(), Mockito.any())).thenReturn(emptyResponse);
		
	
		// when(solrClient.query(Mockito.any())).thenReturn((QueryResponse) getSchemaKeyValuePair());
		// Mockito.when(solrSearchRecordsService.processSearchQuery( Mockito.any(),
		// Mockito.any(), Mockito.anyList())).thenReturn(receivedResponse);
	}
	
	private QueryResponse createAndInitEmptySolrQueryReponseMock() {

		SolrDocumentList sdl = Mockito.mock(SolrDocumentList.class);
		when(sdl.isEmpty()).thenReturn(true);
		QueryResponse response = Mockito.mock(QueryResponse.class);
		when(response.getResults()).thenReturn(sdl);
		when(sdl.getNumFound()).thenReturn(0L);

		return response;
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

		/* Test service when invalid collection is provided as input */
		String invalidCollection = "invalidcollection";
		// setUpMockitoForInvalidCollection(invalidCollection);
		expectedStatusResponse = 400;
		receivedResponse = solrSearchRecordsService.setUpSelectQuerySearchViaQuery(validSchemaColumns, SOLR_COLLECTION,
				"*", "0", "5", "id", "asc");
		assertEquals(expectedStatusResponse, receivedResponse.getStatusCode());
		logger.info("Negative testing is completed for invalid Solr Collection.");
	}

	/*
	 * @Test
	 * 
	 * void testSetUpSelectQueryfieldSearch() {
	 * logger.info("Solr Search ADVANCED service test is started..");
	 * 
	 * int expectedStatusResponse = 200; SearchResponse receivedResponse = null;
	 * 
	 * 
	 * receivedResponse = solrSearchRecordsService.setUpSelectQuerySearchViaQuery(
	 * validSchemaColumns, SOLR_COLLECTION, "*", "0", "5", "id", "asc");
	 * 
	 * assertEquals( expectedStatusResponse, receivedResponse.getStatusCode());
	 * logger.info("Positive testing is completed.");
	 * 
	 * Test service when invalid collection is provided as input String
	 * invalidCollection = "invalidcollection";
	 * //setUpMockitoForInvalidCollection(invalidCollection); expectedStatusResponse
	 * = 400; receivedResponse =
	 * solrSearchRecordsService.setUpSelectQuerySearchViaQueryField(
	 * validSchemaColumns, currentTableSchema, SOLR_COLLECTION, "*", "*", "0", "5",
	 * "id", "asc"); assertEquals( expectedStatusResponse,
	 * receivedResponse.getStatusCode());
	 * logger.info("Negative testing is completed for invalid Solr Collection."); }
	 */

}
