package com.searchservice.app.domain.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class GetCurrentSchemaUtil {
	
	private final Logger log = LoggerFactory.getLogger(GetCurrentSchemaUtil.class);	
	private static final String MICROSERVICE_INTERACT_ISSUE = "Couldn't interact with Ingress microservice to retrieve current schema details";
	private String baseIngressMicroserviceUrl;
	private String tableName;
	private int tenantId;
	
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
	
	public GetCurrentSchemaUtilResponse getTable(String tokenHeaderForIngress) {

			OkHttpClient client = new OkHttpClient();
			String url = baseIngressMicroserviceUrl + "/" + tableName + "?tenantId=" + tenantId;
			log.debug("GET table");
			Request request = new Request.Builder().url(url).addHeader("Authorization", tokenHeaderForIngress)
					.build();
			try {
				Response response = client.newCall(request).execute();
				String responseBody = response.body().string();
				if(!checkIsRequestValid(responseBody)) {
					log.info("Table Retrieved Successfully");
					return new GetCurrentSchemaUtilResponse(true, "Table Retrieved Successfully!",responseBody);
				}else {
					log.error(MICROSERVICE_INTERACT_ISSUE);
					return new GetCurrentSchemaUtilResponse(false, "Ingress Miscroservice Authorization Failed!!", "");
				}
			} catch (IOException e) {
				log.error(MICROSERVICE_INTERACT_ISSUE);
				return new GetCurrentSchemaUtilResponse(false, "Table could not be retrieved! IOException.", "");

			}
	} 
	
	public boolean checkIsRequestValid(String response) {
		JSONObject responseObject = new JSONObject(response);
		boolean isRequestValid = false;
		if(responseObject.has("Unauthorized")) {
			isRequestValid = true;
		}else if(responseObject.has("statusCode") && responseObject.getInt("statusCode") == HttpStatusCode.UNDER_DELETION_PROCESS.getCode()) {
			throw new CustomException(HttpStatusCode.UNDER_DELETION_PROCESS.getCode(),
					HttpStatusCode.UNDER_DELETION_PROCESS, responseObject.getString("message"));
		}
		return isRequestValid;
	}

	public List<String> getCurrentSchemaColumns(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject data = (JSONObject) jsonObject.get("data");
			JSONArray columns = (JSONArray) data.get("columns");

			List<String> currentSchemaColumnNames = new ArrayList<>();
			columns.forEach(col -> {
				JSONObject obj = (JSONObject) col;
				// add custom cols
				currentSchemaColumnNames.add(obj.getString("name"));
			});

			return currentSchemaColumnNames;

		} catch (Exception err) {
			log.error(err.toString());
		}
		return new ArrayList<>();
	}

	public JSONArray getCurrentSchemaDetails(String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject data = (JSONObject) jsonObject.get("data");

			return (JSONArray) data.get("columns");

		} catch (Exception err) {
			log.error("JSON parse error: {}", err.toString());
		}
		return null;
	}
	
}
