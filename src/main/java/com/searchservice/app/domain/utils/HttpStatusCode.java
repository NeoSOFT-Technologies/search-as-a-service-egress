package com.searchservice.app.domain.utils;

public enum HttpStatusCode {

	INVALID_JSON_INPUT(105,"invalid json input or json format"),

	INVALID_TYPE(115, "{} must be of type {}"),
	
	INVALID_FIELD_VALUE(116, "Value for field : {} is not expected as : {}"),
	
    TABLE_NOT_FOUND(108, "does not exist"),
    
    NULL_POINTER_EXCEPTION(404,"Received Null response"),
    
    SERVER_UNAVAILABLE(503,"Unable to Connect To the Server"),
    
    INVALID_QUERY_FIELD(406 , "Query-field validation unsuccessful. Query-field entry can only be in alphanumeric format"),
    
    REQUEST_FORBIDDEN(403, "requested resource is forbidden"),
	
	UNRECOGNIZED_FIELD(106,"Unrecognized Field : {}"),
	
	BAD_REQUEST_EXCEPTION(400,"Bad Request call made. Unable to perform the request");
	
	private int code;
	private String message;
	
	HttpStatusCode(int code, String message) {
		this.code=code;
		this.message=message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	
}
