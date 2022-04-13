package com.searchservice.app;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.domain.dto.logger.Loggers;
import com.searchservice.app.domain.port.api.AdvSearchServicePort;
import com.searchservice.app.domain.port.api.TableServicePort;
import com.searchservice.app.domain.service.AdvSearchService;
import com.searchservice.app.domain.service.SearchService;
import com.searchservice.app.domain.service.TableService;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.infrastructure.adaptor.SearchClientAdapter;
import com.searchservice.app.rest.errors.BadRequestOccurredException;

@IntegrationTest
@AutoConfigureMockMvc
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class SearchServiceTest {

	int statusCode = 0;

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

	private SearchResponse responseDTO = new SearchResponse();

	String jarray = "{\r\n" + "\"schema\" :[\r\n" + "  {\r\n" + "    \"id\" : 1,\r\n"
			+ "    \"name\" : \"The Lightning Thief\",\r\n" + "    \"author\" : \"Rick Riordan\"\r\n" + "  }\r\n"
			+ ",\r\n" + "  {\r\n" + "    \"id\" : 2,\r\n" + "    \"name\" : \"The Sea of Monsters\",\r\n"
			+ "    \"author\" : \"Rick Riordan\" \r\n" + "  }\r\n" + "]\r\n" + "}";

	TableServicePort tableServicePort;

	@MockBean
	AdvSearchService advSearchServicePort;

	@InjectMocks
	SearchService search;

	@MockBean
	LoggerUtils loggerUtils;

	@MockBean
	TableService tableService;
	
	
	SearchClientAdapter searchClientAdapter= new SearchClientAdapter();

	Loggers loggersDTO = new Loggers();

	@SuppressWarnings("deprecation")
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod("nameofCurrMethod");
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename("servicename");
		loggersDTO.setUsername("username");

		searchClientAdapter.getSearchClient( searchQueryfield, tableName);
		searchClientAdapter.getSearchClientAdapter(searchQueryfield, tableName);		
		List<String> currentListOfColumnsOfTableSchema = new ArrayList<>(List.of("id", "category"));
		// tableService = spy(new TableService());
		Mockito.when(tableService.getCurrentTableSchemaColumns(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(currentListOfColumnsOfTableSchema);
		JSONObject jobj = new JSONObject(jarray);
		JSONArray jsonarray = jobj.getJSONArray("schema");
		Mockito.when(tableService.getCurrentTableSchema(Mockito.anyString(), Mockito.anyInt())).thenReturn(jsonarray);
	}

	public void setMockitoSucccessResponseForService() {
		SearchResponse responseDTO = new SearchResponse(statusCode, message);
		responseDTO.setStatusCode(200);

		Mockito.when(advSearchServicePort.setUpSelectQuerySearchViaQuery(Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(
				advSearchServicePort.setUpSelectQuerySearchViaQueryField(Mockito.any(), Mockito.any(), Mockito.any(),
						Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(responseDTO);

	}

	public void setMockitoBadResponseForService() {
		SearchResponse responseDTO = new SearchResponse(statusCode, message);
		responseDTO.setStatusCode(400);
		Mockito.when(advSearchServicePort.setUpSelectQuerySearchViaQuery(Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(
				advSearchServicePort.setUpSelectQuerySearchViaQueryField(Mockito.any(), Mockito.any(), Mockito.any(),
						Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(responseDTO);

	}

	
	@Test
	void testsearchQuery() throws Exception {
		int statusCode = 200;
		setMockitoSucccessResponseForService();
		SearchResponse responseDTO = search.searchQuery(tenantId, tablename1, searchQuery, startRecord, pageSize,
				orderBy, order, loggersDTO);
		assertEquals(statusCode, responseDTO.getStatusCode());

	}

	@Test
	void testsearchQueryfield() throws Exception {
		int statusCode = 200;
		setMockitoSucccessResponseForService();

		SearchResponse responseDTO = search.searchField(tenantId, tablename1, "*", "*", startRecord, pageSize, orderBy,
				order, loggersDTO);
		assertEquals(statusCode, responseDTO.getStatusCode());

	}

	@Test
	void testsearchbadQuery() throws Exception {
		int statusCode = 400;
		setMockitoBadResponseForService();
		SearchResponse responseDTO = search.searchQuery(tenantId, "1", searchQuery, startRecord, pageSize, orderBy,
				order, loggersDTO);

		assertEquals(statusCode, responseDTO.getStatusCode());

	}
	 
	@Test
	void testsearchBadQueryfield() throws Exception {
		int statusCode =400;
		setMockitoBadResponseForService();
try {
		SearchResponse responseDTO = search.searchField(tenantId, "1", "*", "*", startRecord, pageSize, orderBy, order,
				loggersDTO);		
		assertEquals(statusCode, responseDTO.getStatusCode());
}catch (Exception e) {
	
}

	}

}
