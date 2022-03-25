package com.searchservice.app.domain.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response{

    private int statusCode;
    private String message;
    private String token;

    public Response(String token) {
        this.token = token;
    }
    
    public Response(int responseStatusCode, String responseMessage) {
    	this.statusCode = responseStatusCode;
    	this.message = responseMessage;
    }

	public Response(int statusCode, String token, String message) {
		this.statusCode = statusCode;
		this.token = token;
		this.message = message;
	}
	
}
