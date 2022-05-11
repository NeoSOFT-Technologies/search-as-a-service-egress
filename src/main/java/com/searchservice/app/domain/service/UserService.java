package com.searchservice.app.domain.service;

import java.util.Arrays;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.user.UserDTO;
import com.searchservice.app.domain.port.api.UserServicePort;


@Service
public class UserService implements UserServicePort{
	
	private static final String ERROR = "error";

	@Autowired
	RestTemplate restTemplate;
	
	@Value("${base-token-url}")
	private String baseTokenUrl;
	
	@Override
	public Response getToken(UserDTO user) {
		if (user.getUsername().isBlank() || user.getUsername().isEmpty() || user.getPassword().isBlank() || user.getPassword().isEmpty()) {
			return createResponse(ERROR, "username and password must bot be blank.", 
					400);
		}
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    HttpEntity<UserDTO> request = new HttpEntity<>(user,headers);
	    ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
	    try {
			response = restTemplate.postForEntity(baseTokenUrl, request, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			return createResponse(null, "Invalid credentials", 400);
		}
		JSONObject obj = new JSONObject(response.getBody());
		if (obj.has("access_token")) {
			String accessToken = obj.getString("access_token");
			return createResponse(accessToken, "Token is generated successfully", 200);
		}
		if (obj.has(ERROR)) {
			String errorDesc = obj.getString("error_description");
			String error = obj.getString(ERROR);
			return createResponse(error, errorDesc, 400);
		}
		return createResponse(null, "Something went wrong! Please try again...", 400);
	}

	public Response createResponse(String token, String message, int statusCode) {
		Response responseDTO = new Response(token);
		responseDTO.setMessage(message);
		responseDTO.setStatusCode(statusCode);
		return responseDTO;
	}

}