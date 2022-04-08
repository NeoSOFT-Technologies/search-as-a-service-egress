package com.searchservice.app.domain.utils;

import com.searchservice.app.domain.service.JwtTokenFilterService;
import com.squareup.okhttp.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
public class GetCurrentSchemaUtil {

	private final Logger log = LoggerFactory.getLogger(GetCurrentSchemaUtil.class);

	private String baseIngressMicroserviceUrl;
	private String tableName;
	private int clientId;
	
	JwtTokenFilterService service =new JwtTokenFilterService();
	
	public GetCurrentSchemaUtilResponse get() {
		
		OkHttpClient client = new OkHttpClient();
		String url = baseIngressMicroserviceUrl + "/"+clientId + "/"+tableName;
		log.debug("GET table");
		Request request = new Request.Builder().url(url).build();

		try {
			Response response = client.newCall(request).execute();
			return new GetCurrentSchemaUtilResponse(
					true, "Table Retrieved Successfully!", response.body().string());

		} catch (IOException e) {
			log.error("Couldn't interact with microservice to retrieve current schema details!");
			return new GetCurrentSchemaUtilResponse(
					false, "Table could not be retrieved! IOException.", "");
		}

	}
	
	
    

	@Data
	public static class GetCurrentSchemaUtilResponse {
		boolean isTableRetrieved;
		String message;
		String responseString;
		

		public GetCurrentSchemaUtilResponse(boolean isTableRetrieved, String message, String responseString) {
			this.isTableRetrieved = isTableRetrieved;
			this.message = message;
			this.responseString = responseString;
		}
	}
	
	
	public List<String> getCurrentSchemaColumns(String response) {
		try {
		    JSONObject jsonObject = new JSONObject(response);	
		    JSONObject data = (JSONObject) jsonObject.get("data");
		    JSONArray columns = (JSONArray) data.get("columns");
		    
		    List<String> currentSchemaColumnNames = new ArrayList<>();
		    columns.forEach(col -> {
		    	JSONObject obj = (JSONObject)col;
		    	// add custom cols
		    	currentSchemaColumnNames.add(obj.getString("name"));
		    });

			return currentSchemaColumnNames;
		     
		}catch (Exception err){
			log.error(err.toString());
		}
		return new ArrayList<>();
	}
	
	public JSONArray getCurrentSchemaDetails(String response) {
		try {
		    JSONObject jsonObject = new JSONObject(response);	
		    JSONObject data = (JSONObject) jsonObject.get("data");
		    
			return (JSONArray) data.get("columns");
		     
		}catch (Exception err){
			log.error(err.toString());
		}
		return new JSONArray();
	}
	
}
