package com.searchservice.app.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.searchservice.app.IntegrationTest;
import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.domain.service.SearchService;
import com.searchservice.app.domain.utils.HttpStatusCode;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class SearchServiceResourceTest {

	@Value("${base-url.api-endpoint.home}")
	private String apiEndpoint;

	int statusCode = 0;
	String name;
	String message = "";
	int tenantId = 101;
	String tableName = "book";
	String tablename1 = "book_101";
	String searchQuery = "category: shubham AND category: karthik";
	String startRecord = "0";
	String pageSize = "10";
	String orderBy = "id";
	String order = "asc";
	String searchQueryfield = "category";
	String queryFieldSearchTerm = "shubham";
	String expectedGetResponse = "{\r\n" + "  \"statusCode\": 200,\r\n"
			+ "  \"message\": \"Records fetched successfully\",\r\n" + "  \"status\": \"OK\",\r\n"
			+ "  \"results\": {\r\n" + "    \"numDocs\": 1,\r\n" + "    \"data\": [\r\n" + "      {\r\n"
			+ "        \"id\": \"7\",\r\n" + "        \"title\": \"the aaaaaaaaaa\",\r\n"
			+ "        \"category\": [\r\n" + "          \"shubham\",\r\n" + "          \"mangesh\",\r\n"
			+ "          \"karthik\",\r\n" + "          \"abc\"\r\n" + "        ],\r\n"
			+ "        \"_version_\": 1726096559946334200\r\n" + "      }\r\n" + "    ]\r\n" + "  }\r\n" + "}";

	String expectedCreateResponse400 = "{\r\n" + "  \"statusCode\": 400,\r\n" + "  \"name\": \"booksdfsd\",\r\n"
			+ "  \"message\": \"Unable to get the Schema. Please check the collection name again!\"\r\n" + "}";

	String inputString = "[{\"shares\":20000,\"manufacture\":\"warren buffet\",\"website\":\"flipkart.com\",\"color\":\"blue\",\"author\":\"dhanashree\",\"id\":24}]";

	@Autowired
	MockMvc restAMockMvc;

	@MockBean
	SearchService searchservice;

	public void setMockitoSucccessResponseForService() {
		SearchResponse responseDTO = new SearchResponse(statusCode, message);
		responseDTO.setStatusCode(200);
		Mockito.when(searchservice.searchQuery(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(responseDTO);
		Mockito.when(searchservice.searchField(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(responseDTO);
	}

	public void setMockitoBadResponseForService() {
		SearchResponse responseDTO = new SearchResponse(statusCode, message);
		responseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		Mockito.when(searchservice.searchQuery(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(responseDTO);
		Mockito.when(searchservice.searchField(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(responseDTO);
	}

	@Test
	void testsearchRecordsViaQuery() throws Exception {
		setMockitoSucccessResponseForService();
		restAMockMvc
				.perform(MockMvcRequestBuilders.get(apiEndpoint + "/query/" + tableName + "/?tenantId=" + tenantId)
						.contentType(MediaType.APPLICATION_PROBLEM_JSON).content(inputString))
				.andExpect(status().isOk());
	}

	@Test
	void testsearchRecordsViaQueryFields() throws Exception {
		setMockitoSucccessResponseForService();
		restAMockMvc
				.perform(MockMvcRequestBuilders.get(apiEndpoint + "/" + tableName + "/?tenantId=" + tenantId)
						.contentType(MediaType.APPLICATION_PROBLEM_JSON).content(inputString))
				.andExpect(status().isOk());
	}

	@Test
	void testbadsearchRecordsViaQuery() throws Exception {
		setMockitoBadResponseForService();
		restAMockMvc
				.perform(MockMvcRequestBuilders.get(apiEndpoint + "/query/" + tableName + "/?tenantId=" + tenantId)
						.contentType(MediaType.APPLICATION_PROBLEM_JSON).content(inputString))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testbadsearchRecordsViaQueryFields() throws Exception {
		setMockitoBadResponseForService();
		restAMockMvc
				.perform(MockMvcRequestBuilders.get(apiEndpoint + "/" + tableName + "/?tenantId=" + tenantId)
						.contentType(MediaType.APPLICATION_PROBLEM_JSON).content(inputString))
				.andExpect(status().isBadRequest());

	}

}