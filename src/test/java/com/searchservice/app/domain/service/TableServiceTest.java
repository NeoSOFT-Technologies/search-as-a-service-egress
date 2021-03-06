package com.searchservice.app.domain.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.searchservice.app.domain.dto.IngressSchemaResponse;
import com.searchservice.app.domain.utils.GetCurrentSchemaUtil;
import com.searchservice.app.domain.utils.GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse;

@AutoConfigureMockMvc
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class TableServiceTest {

	@Value("${base-microservice-url}")
	private String baseIngressMicroserviceUrl;
	@Value("${microservice-url.get-table}")
	private String getTableUrl;

	// mock HttpServletRequest
	HttpServletRequest request = mock(HttpServletRequest.class);

	int statusCode = 0;

	String message = "";
	int tenantId = 101;
	String tableName = "book";
	String tablename1 = "book_101";
	
	@InjectMocks
	TableService tableService;

	Map<String, String> headers = new HashMap<>();

	String json = "{\r\n" + "\"books\" :[\r\n" + "  {\r\n" + "    \"id\" : 1,\r\n"
			+ "    \"name\" : \"queryField\",\r\n" + "    \"author\" : \"Rick Riordan\",\r\n"
			+ "    \"multiValue\" : true\r\n" + "  }  \r\n" + "]\r\n" + "}";

	JSONObject jobj = new JSONObject(json);

	@MockBean
	GetCurrentSchemaUtil getCurrentSchemaUtil;

	@MockBean
	GetCurrentSchemaUtilResponse getCurrentSchemaUtilResponse;

	@SuppressWarnings("deprecation")
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		// Mockito.when(request.newBuilder().url(url).build()).thenReturn(request);
	}

	public void setMockitoSucccessResponseForService() {
		getCurrentSchemaUtilResponse = new GetCurrentSchemaUtilResponse(true, message, "book");
		JSONArray jarray = jobj.getJSONArray("books");
		Mockito.when(getCurrentSchemaUtil.getTable(Mockito.anyString())).thenReturn(getCurrentSchemaUtilResponse);
		Mockito.when(getCurrentSchemaUtil.getCurrentSchemaColumns(Mockito.any()))
				.thenReturn(new LinkedList<>(List.of("book")));
		Mockito.when(getCurrentSchemaUtil.getCurrentSchemaDetails(Mockito.any())).thenReturn(jarray);
		Mockito.when(request.getHeader(Mockito.anyString())).thenReturn("Bearer TokenTest");
	}

	@Test
	void testsearchtableSchema() throws Exception {
		List<String> currentListOfColumnsOfTableSchema = new LinkedList<>(List.of("book"));
		setMockitoSucccessResponseForService();
		List<String> responseDTO = tableService.getCurrentTableSchemaColumns(tableName, tenantId);
		assertEquals(responseDTO, currentListOfColumnsOfTableSchema);

	}

	@Test
	void testsearchtableSchemacolumn() throws Exception {
		JSONArray jarray = jobj.getJSONArray("books");
		setMockitoSucccessResponseForService();
		IngressSchemaResponse ingressResponse = tableService.getCurrentTableSchema(tableName, tenantId);
		JSONArray responseDTO = ingressResponse.getJsonArray();
		assertEquals(responseDTO, jarray);
	}

	@Test
	void testsearchtableSchemacolumnMap() throws Exception {
		String jarray = "{author=Rick Riordan, id=1, multiValue=true}";
		setMockitoSucccessResponseForService();
		Map<String, Object> mapDoc = new HashMap<String, Object>();
		mapDoc.put("id", "1");
		mapDoc.put("name", "queryField");
		mapDoc.put("author", "Rick Riordan");
		mapDoc.put("multiValue", "true");
		List<String> validColumns = new ArrayList<>(List.of("author", "id", "multiValue", "Power"));
		Map<String, Object> responseDTO = tableService.getValidMapOfDocument(mapDoc, validColumns);
		String Mapstr = responseDTO.toString();
		assertEquals(Mapstr, jarray);
	}

}
