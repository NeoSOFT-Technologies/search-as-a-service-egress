package com.searchservice.app.domain.security;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.searchservice.app.rest.errors.BadRequestOccurredException;


@Service
public class KeycloakAuthService {

	public JSONObject getDecodedTokenPayloadJson(String token) {
		
		String payload = token.split("\\.")[1];
		
		String decodedPayload = new String(Base64.decodeBase64(payload), StandardCharsets.UTF_8);

		try {
			return new JSONObject(decodedPayload);
		} catch(JSONException e) {
			throw new BadRequestOccurredException(400, "Invalid Json Format");
		}
	}
	
	
	public List<String> getActiveUserPermissions(JSONObject tokenPayload) {
		JSONArray permissions = tokenPayload.getJSONArray("permission");
		
		return permissions.toList().stream().map(p -> Objects.toString(p, null)).collect(Collectors.toList());
	}
	
	
	public boolean isReadPermissionGranted(String token) {
		
		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		List<String> permissions = getActiveUserPermissions(tokenPayload);

		return permissions.contains(KeycloakPermissionsConstants.p1);
	}
	
	
	public boolean isWritePermissionGranted(String token) {
		
		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		List<String> permissions = getActiveUserPermissions(tokenPayload);

		return permissions.contains(KeycloakPermissionsConstants.p2);
	}
	
	
	public boolean isUpdatePermissionGranted(String token) {
		
		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		List<String> permissions = getActiveUserPermissions(tokenPayload);

		return permissions.contains(KeycloakPermissionsConstants.p3);
	}
	
	
	public boolean isDeletePermissionGranted(String token) {
		
		JSONObject tokenPayload = getDecodedTokenPayloadJson(token);
		List<String> permissions = getActiveUserPermissions(tokenPayload);

		return permissions.contains(KeycloakPermissionsConstants.p4);
	}
	
}
