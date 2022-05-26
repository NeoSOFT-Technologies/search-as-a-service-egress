package com.searchservice.app.domain.service;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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

import com.searchservice.app.IntegrationTest;
import com.searchservice.app.domain.dto.IngressSchemaResponse;
import com.searchservice.app.domain.dto.SearchResponse;
import com.searchservice.app.domain.port.api.TableServicePort;
import com.searchservice.app.domain.utils.HttpStatusCode;
import com.searchservice.app.infrastructure.adaptor.SearchClientAdapter;
import com.searchservice.app.rest.errors.CustomException;

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
	TableService tableService;
	
	
	SearchClientAdapter searchClientAdapter= new SearchClientAdapter();

	@SuppressWarnings("deprecation")
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		searchClientAdapter.getSearchClient( searchQueryfield, tableName);
		searchClientAdapter.getSearchClientAdapter(searchQueryfield, tableName);		
		List<String> currentListOfColumnsOfTableSchema = new ArrayList<>(List.of("id", "category"));
		// tableService = spy(new TableService());
		Mockito.when(tableService.getCurrentTableSchemaColumns(Mockito.anyString(), Mockito.anyInt()))
				.thenReturn(currentListOfColumnsOfTableSchema);
		JSONObject jobj = new JSONObject(jarray);
		JSONArray jsonarray = jobj.getJSONArray("schema");
		IngressSchemaResponse ingressResponse = new IngressSchemaResponse(jsonarray, "message");
		Mockito.when(tableService.getCurrentTableSchema(Mockito.anyString(), Mockito.anyInt())).thenReturn(ingressResponse);
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
		responseDTO.setStatusCode(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode());
		Mockito.when(advSearchServicePort.setUpSelectQuerySearchViaQuery(Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
		Mockito.when(
				advSearchServicePort.setUpSelectQuerySearchViaQueryField(Mockito.any(), Mockito.any(), Mockito.any(),
						Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(responseDTO);

	}

	public void setMockitoNullResponse() {
		Mockito.when(
				advSearchServicePort.setUpSelectQuerySearchViaQueryField(Mockito.any(), Mockito.any(), Mockito.any(),
						Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(null);
		Mockito.when(advSearchServicePort.setUpSelectQuerySearchViaQuery(Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null);
	}
	
	public void setMockitoServiceUnavailableResponse() {
		SearchResponse responseDTO = new SearchResponse(statusCode, message);
		responseDTO.setStatusCode(HttpStatusCode.SERVER_UNAVAILABLE.getCode());
		Mockito.when(
				advSearchServicePort.setUpSelectQuerySearchViaQueryField(Mockito.any(), Mockito.any(), Mockito.any(),
						Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(responseDTO);
		Mockito.when(advSearchServicePort.setUpSelectQuerySearchViaQuery(Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
	}
	
	public void setMockitoServiceRequestForbidden() {
		SearchResponse responseDTO = new SearchResponse();
		responseDTO.setStatusCode(HttpStatusCode.REQUEST_FORBIDDEN.getCode());
		Mockito.when(
				advSearchServicePort.setUpSelectQuerySearchViaQueryField(Mockito.any(), Mockito.any(), Mockito.any(),
						Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(responseDTO);
		Mockito.when(advSearchServicePort.setUpSelectQuerySearchViaQuery(Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(responseDTO);
	} 
	@Test
	void testsearchQuery() throws Exception {
		int statusCode = 200;
		setMockitoSucccessResponseForService();
		SearchResponse responseDTO = search.searchQuery(tenantId, tablename1, searchQuery, startRecord, pageSize,
				orderBy, order);
		assertEquals(statusCode, responseDTO.getStatusCode());

	}

	@Test
	void testsearchQueryfield() throws Exception {
		int statusCode = 200;
		setMockitoSucccessResponseForService();

		SearchResponse responseDTO = search.searchField(tenantId, tablename1, "*", "*", startRecord, pageSize, orderBy,
				order);
		assertEquals(statusCode, responseDTO.getStatusCode());

	}
	

	@Test
	void testsearchQueryfieldNull(){
		setMockitoNullResponse();
        try {
		 search.searchField(tenantId, tablename1, "*", "*", startRecord, pageSize, orderBy,
				order);
        }catch(CustomException e) {
        	assertEquals(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), e.getExceptionCode());
        }
	}
	
	
	@Test
	void testsearchQueryfieldServiceUnavailable(){
		setMockitoServiceUnavailableResponse();
        	SearchResponse responseDTO = search.searchField(tenantId, tablename1, "*", "*", startRecord, pageSize, orderBy,
				order);
        	assertEquals(HttpStatusCode.SERVER_UNAVAILABLE.getCode(), responseDTO.getStatusCode());
	}
	
	@Test
	void testsearchQueryfieldRequestForbidden(){
		setMockitoServiceRequestForbidden();
        try {
		 search.searchField(tenantId, tablename1, "*", "*", startRecord, pageSize, orderBy,
				order);
        }catch(CustomException e) {
        	assertEquals(HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode(), e.getExceptionCode());
        }
	}

	@Test
	void testsearchbadQuery() throws Exception {
		int statusCode = HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode();
		setMockitoBadResponseForService();
		try {
		 search.searchQuery(tenantId, "1", searchQuery, startRecord, pageSize, orderBy,
				order);
		}catch(CustomException e) {
			assertEquals(statusCode, e.getExceptionCode());
		}
	}
	

	@Test
	void testsearchQueryNull(){
		setMockitoNullResponse();
		try {
		  search.searchQuery(tenantId, "1", searchQuery, startRecord, pageSize, orderBy,
				order);
		}catch(CustomException e) {
			assertEquals(HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), e.getExceptionCode());
		}
	}
	
	@Test
	void testsearchQueryServiceUnavailable(){
		setMockitoServiceUnavailableResponse();
        	SearchResponse responseDTO =   search.searchQuery(tenantId, "1", searchQuery, startRecord, pageSize, orderBy,
    				order);
        	assertEquals(HttpStatusCode.SERVER_UNAVAILABLE.getCode(), responseDTO.getStatusCode());
	}
	
	
	 
	@Test
	void testsearchBadQueryfield() throws Exception {
		int statusCode = HttpStatusCode.BAD_REQUEST_EXCEPTION.getCode();
		setMockitoBadResponseForService();
try {
		SearchResponse responseDTO = search.searchField(tenantId, "1", "*", "*", startRecord, pageSize, orderBy, order);		
		assertEquals(statusCode, responseDTO.getStatusCode());
}catch (Exception e) {
	
}

	}

}
