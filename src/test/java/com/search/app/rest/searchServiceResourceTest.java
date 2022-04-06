package com.search.app.rest;



import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.search.app.service.IntegrationTest;
import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.domain.service.SearchService;
import com.searchservice.app.domain.utils.LoggerUtils;


@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class searchServiceResourceTest {

	// String apiEndpoint = "/api/v1";
	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;

	
	int statusCode=0;
	String name;
	String message="";
	int tenantId = 101;
	String tableName = "book";
	String expectedGetResponse = "{\r\n"
			+ "  \"statusCode\": 200,\r\n"
			+ "  \"message\": \"Records fetched successfully\",\r\n"
			+ "  \"status\": \"OK\",\r\n"
			+ "  \"results\": {\r\n"
			+ "    \"numDocs\": 1,\r\n"
			+ "    \"data\": [\r\n"
			+ "      {\r\n"
			+ "        \"id\": \"7\",\r\n"
			+ "        \"title\": \"the aaaaaaaaaa\",\r\n"
			+ "        \"category\": [\r\n"
			+ "          \"shubham\",\r\n"
			+ "          \"mangesh\",\r\n"
			+ "          \"karthik\",\r\n"
			+ "          \"abc\"\r\n"
			+ "        ],\r\n"
			+ "        \"_version_\": 1726096559946334200\r\n"
			+ "      }\r\n"
			+ "    ]\r\n"
			+ "  }\r\n"
			+ "}";

	String expectedCreateResponse400 = "{\r\n" + "  \"statusCode\": 400,\r\n" + "  \"name\": \"booksdfsd\",\r\n"
			+ "  \"message\": \"Unable to get the Schema. Please check the collection name again!\"\r\n" + "}";

	String inputString = "[{\"shares\":20000,\"manufacture\":\"warren buffet\",\"website\":\"flipkart.com\",\"color\":\"blue\",\"author\":\"dhanashree\",\"id\":24}]";

    @Autowired
        MockMvc restAMockMvc;

    @SuppressWarnings("deprecation")
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		String timestamp = LoggerUtils.utcTime().toString();
		SearchResponse responseDTO = new SearchResponse(statusCode, message);
		responseDTO.setStatusCode(200);
		Mockito.when(searchservice.searchQuery(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any() )).thenReturn(responseDTO);
			
			}

	
	@MockBean
	 SearchService searchservice;

		/*
		 * public void setMockitoSucccessResponseForService() { SearchResponse
		 * responseDTO = new SearchResponse(statusCode, message);
		 * responseDTO.setStatusCode(200);
		 * Mockito.when(searchservice.searchQuery(Mockito.any(), Mockito.any(),
		 * Mockito.any(), Mockito.any(),Mockito.any(), Mockito.any(), Mockito.any(),
		 * Mockito.any())).thenReturn(responseDTO); }
		 * 
		 * public void setMockitoBadResponseForService() { SearchResponse responseDTO =
		 * new SearchResponse(statusCode, message); responseDTO.setStatusCode(400); }
		 */
	@Test
	void testinputdocs() throws Exception {
		System.out.println("vfdvdfvfdv        "+apiEndpoint);

		restAMockMvc.perform(MockMvcRequestBuilders.get(apiEndpoint + "/query/" + tenantId + "/" + tableName)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				)
				.andExpect(status().isOk());
	}


	
	

	
	
}