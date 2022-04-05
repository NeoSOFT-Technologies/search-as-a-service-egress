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

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Value;
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
import com.searchservice.app.domain.utils.GetCurrentSchemaUtil;
import com.searchservice.app.domain.utils.GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse;
import com.searchservice.app.domain.utils.LoggerUtils;
import com.searchservice.app.rest.errors.BadRequestOccurredException;
import com.squareup.okhttp.Request;


@AutoConfigureMockMvc
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class TableServiceTest {
	

	@Value("${base-microservice-url}")
	private String baseIngressMicroserviceUrl;
	@Value("${microservice-url.get-table}")
	private String getTableUrl;
	

	int statusCode = 0;

	String message = "";
	int tenantId = 101;
	String tableName = "book";
	String tablename1 = "book_101";

	private SearchResponse responseDTO = new SearchResponse();
	@InjectMocks
	TableService tableService;

	Loggers loggersDTO = new Loggers();
	
	String token = "Unauthorized:Invalid token";
	
	@Mock
	private GetCurrentSchemaUtil getCurrentSchemaUtil;
	
	GetCurrentSchemaUtilResponse getCurrentSchemaUtilResponse = new GetCurrentSchemaUtilResponse(true,message,"book");

	@SuppressWarnings("deprecation")
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		String timestamp = LoggerUtils.utcTime().toString();
		loggersDTO.setNameofmethod("nameofCurrMethod");
		loggersDTO.setTimestamp(timestamp);
		loggersDTO.setServicename("servicename");
		loggersDTO.setUsername("username");

		responseDTO = new SearchResponse();
		responseDTO.setMessage("success");
		responseDTO.setStatusCode(200);		
		String url = baseIngressMicroserviceUrl + "/"+tenantId + "/"+tableName;
		Request request = new Request.Builder().url(url).build();
		
		GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse respons = new GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse(true, message, token);
	
		 //getCurrentSchemaUtilResponse.setResponseString("Unauthorized:Invalid token");
		Mockito.when(getCurrentSchemaUtil.get()).thenReturn(respons);
			}

	public void setMockitoSucccessResponseForService() {
		SearchResponse responseDTO = new SearchResponse(statusCode, message);
		responseDTO.setStatusCode(200);
	
	}

	public void setMockitoBadResponseForService() {
		SearchResponse responseDTO = new SearchResponse(statusCode, message);
		responseDTO.setStatusCode(400);
		
	}

	
	@Test
	void testsearchtableSchema() throws Exception {
		List<String> currentListOfColumnsOfTableSchema = new ArrayList<>(List.of("id", "category"));
		
		List<String> responseDTO = tableService.getCurrentTableSchemaColumns( tableName,tenantId);
		assertEquals(responseDTO,currentListOfColumnsOfTableSchema );

	}
	
	/*
	 * @Test void testsearchtable() throws Exception { List<String>
	 * currentListOfColumnsOfTableSchema = new ArrayList<>(List.of("id",
	 * "category"));
	 * 
	 * JSONArray responseDTO = tableService.getCurrentTableSchema(
	 * tablename1,tenantId);
	 * assertEquals(responseDTO,currentListOfColumnsOfTableSchema );
	 * 
	 * }
	 */
	
	
	

}
