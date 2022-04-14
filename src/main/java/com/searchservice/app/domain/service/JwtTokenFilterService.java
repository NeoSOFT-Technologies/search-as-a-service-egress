package com.searchservice.app.domain.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.domain.utils.KeycloakConfigProperties;

//@Component
public class JwtTokenFilterService extends OncePerRequestFilter{
	
//	private String realm_name;
//	private String client_id;
//	private String client_Secret;
	private RestTemplate restTemplate;
	private KeycloakConfigProperties keycloakConfigProperties;
	private final Logger log = LoggerFactory.getLogger(JwtTokenFilterService.class);
	private ObjectMapper mapper = new ObjectMapper();
	Map<String, Object> errorDetails = new HashMap<>();
	
	public JwtTokenFilterService() {
		super();
	}
	public JwtTokenFilterService(KeycloakConfigProperties keycloakConfigProperties, RestTemplate restTemplate) {
		super();
		this.keycloakConfigProperties = keycloakConfigProperties;
		this.restTemplate = restTemplate;
	}

	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
		
      // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("[JwtTokenFilterService][doFilterInternal] Authorization Header Value : "+header);
        if (null == header || header.isEmpty() || !header.startsWith("Bearer ")) {
        	errorDetails.put("Unauthorized", "Invalid token");
	        response.setStatus(HttpStatus.UNAUTHORIZED.value());
	        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	        mapper.writeValue(response.getWriter(), errorDetails);
            return;
        }

      // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        log.info("[JwtTokenFilterService][doFilterInternal] Token Value : "+token);
        if (!validate(token, response)) {
        	errorDetails.put("Unauthorized", "Invalid token");
	        response.setStatus(HttpStatus.UNAUTHORIZED.value());
	        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	        mapper.writeValue(response.getWriter(), errorDetails);
            return;
        }else {
        	chain.doFilter(request, response);
        }
    }
	 
	 private boolean validate(String token, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {
		 boolean active = false;
		try {
			String url = keycloakConfigProperties.getAuth_server_url()+"/realms/"+keycloakConfigProperties.getRealm()+"/protocol/openid-connect/token/introspect";
	    	log.info("[JwtTokenFilterService][validate] Token Value : "+token);
	    	log.info("[JwtTokenFilterService][validate] realm_name : "+keycloakConfigProperties.getRealm());
	    	log.info("[JwtTokenFilterService][validate] client_Secret : "+keycloakConfigProperties.getCredentials().getSecret());
	    	log.info("[JwtTokenFilterService][validate] Auth-Server-Url : "+keycloakConfigProperties.getAuth_server_url());
	    	log.info("[JwtTokenFilterService][validate] url : "+url);
	    	
	    // creating and setting the Header
	    	HttpHeaders headers = new HttpHeaders();
	    	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    	
	    // creating Body parameters	
	    	MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
	    	map.add("token", token);
	    	map.add("client_id", keycloakConfigProperties.getResource());
	    	map.add("client_secret", keycloakConfigProperties.getCredentials().getSecret());
	    	
	    // Creating HttpEntity and set header and body
	    	HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
	    
	    // Consuming rest API
	    	ResponseEntity<String> keycloakResponse = restTemplate.postForEntity( url, request , String.class );
	    	log.info("[JwtTokenFilterService][validate] Final Response : "+keycloakResponse.getBody());
	    	JSONObject obj = new JSONObject(keycloakResponse.getBody());
	    	active = obj.getBoolean("active");
	    	log.info("[JwtTokenFilterService][validate] active : "+active);
		} catch (Exception e) {
			if(null != e.getMessage() && e.getMessage().contains("Connection refused")) {
				errorDetails.put("Unauthorized", "Invalid token : Unable to connect with Keycloak server");
		        response.setStatus(HttpStatus.UNAUTHORIZED.value());
		        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		        mapper.writeValue(response.getWriter(), errorDetails);
			}
		}	
        return active;
    }
	
}