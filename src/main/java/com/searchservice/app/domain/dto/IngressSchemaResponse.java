package com.searchservice.app.domain.dto;


import org.json.JSONArray;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngressSchemaResponse{

    private JSONArray jsonArray;
    private String message;

    
    public IngressSchemaResponse(JSONArray jsonArray, String message) {
    	this.jsonArray = jsonArray;
    	this.message = message;
    }
}
