package com.Searchutil.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.searchservice.app.domain.utils.GetCurrentSchemaUtil;
import com.searchservice.app.domain.utils.GetCurrentSchemaUtil.GetCurrentSchemaUtilResponse;
import com.squareup.okhttp.OkHttpClient;


public class GetCurrentSchemaUtilTest {
	private final Logger logger = LoggerFactory.getLogger(GetCurrentSchemaUtil.class); 
	 @MockBean
	 private OkHttpClient okHttpClient;
	 
	GetCurrentSchemaUtil schemaUtil = new GetCurrentSchemaUtil();	
	GetCurrentSchemaUtil schemaUtil1 = new GetCurrentSchemaUtil();	
	GetCurrentSchemaUtilResponse response1=new GetCurrentSchemaUtilResponse(false, "Test", "testimg");
	GetCurrentSchemaUtilResponse response2=new GetCurrentSchemaUtilResponse(false, "Tester", "testimgf");
	String schemaResponse = "{\"statusCode\":200,\"message\":\"TableInformationretrievedsuccessfully\","
			+ "\"data\":{\"tableName\":\"TestTable\",\""
			+ "columns\":"
			+ "[{\"name\":\"empName\",\"type\":\"strings\",\"partialSearch\":false,"
			+ "\"sortable\":false,\"storable\":true,\"multiValue\":true,\"filterable\":false,\"required\":true},"
			+ "{\"name\":\"id\",\"type\":\"string\",\"partialSearch\":false,"
			+ "\"sortable\":false,\"storable\":true,\"multiValue\":false,\"filterable\":true,\"required\":true}]}}";
	
	@Test
	void testgetCurrentSchemaColumns()
	{
		System.out.println("Testing Egress : "+schemaUtil.toString());
		System.out.println("Testing Egress: "+schemaUtil.equals(schemaUtil1));
		System.out.println("testing egress: "+schemaUtil.hashCode());

		int expectedSize =2;
		
		List<String> schemaColumns  = schemaUtil.getCurrentSchemaColumns(schemaResponse);
		assertEquals(expectedSize, schemaColumns.size());
	}
	
	@Test
	void testgetCurrentSchemaDetails()
	{
		logger.info("Testing Egress : "+response1.toString());
		
		logger.info("Testing Egress: "+response1.equals(response2));
		logger.info("testing egress: "+response1.hashCode());
		logger.info("Testing egress");
		logger.info("HEllo");
		response1.setTableRetrieved(true);
		JSONArray schemaDetails  = schemaUtil.getCurrentSchemaDetails(schemaResponse);
		assertNotNull(schemaDetails);
	}
	
	@Test
	void testgetCurrentSchemaColumnsException()
	{
		int expectedSizeColumnsException =0;
		List<String> schemaColumns  = schemaUtil.getCurrentSchemaColumns("");
		assertEquals(expectedSizeColumnsException, schemaColumns.size());
	}
	
	@Test
	void testgetCurrentSchemaDetailsException()
	{
		int expectedSizeDetailsException =0;
		JSONArray schemaDetails  = schemaUtil.getCurrentSchemaDetails("");
		assertEquals(expectedSizeDetailsException, schemaDetails.length());
	}
	
	@Test
	void testGetException() {
		schemaUtil.setBaseIngressMicroserviceUrl("http:local:8081");
		schemaUtil.setTableName("TestTable");
		schemaUtil.setClientId(101);
		assertEquals(false,schemaUtil.get().isTableRetrieved());
	}

}
