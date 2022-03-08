package com.searchservice.app.domain.dto;

import java.util.List;

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
    private String name;
    private String message;
    private List<String> data;

    public Response(String name) {
        this.name = name;
    }
    
    public Response(int responseStatusCode, String responseMessage) {
    	this.statusCode = responseStatusCode;
    	this.message = responseMessage;
    }

	public Response(int statusCode, String name, String message) {
		this.statusCode = statusCode;
		this.name = name;
		this.message = message;
	}
	
}
