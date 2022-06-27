package com.searchservice.app.rest.errors;

public enum HttpStatusCode {

	// With Custom Error Status Code
	INVALID_JSON_INPUT(105,"invalid json input or json format"),
	
	UNRECOGNIZED_FIELD(106,"Unrecognized Field : {}"), 

	UNDER_DELETION_PROCESS(107,"under deletion process"),
	
	TABLE_NOT_FOUND(108, "does not exist"), 
	
	JSON_PARSE_EXCEPTION(114, "JSON parse error occurred"), 
	
	INVALID_TYPE(115, "{} must be of type {}"), 
	
	INVALID_FIELD_VALUE(116, "Value for field : {} is not expected as : {}"), 
	
	INVALID_QUERY_FORMAT(117, "Couldn't parse the search query."), 
	
	INVALID_QUERY_FIELD(118, "Query-field validation unsuccessful. Query-field entry can only be in alphanumeric format"), 
    
	SAAS_SERVER_ERROR(119, "This feature is currently down. Try again later"), 
	
	INVALID_CREDENTIALS(121, "Invalid credentials provided"), 
	
	// With Primitive Error Status Code
	BAD_REQUEST_EXCEPTION(400,"Bad Request call made. "), 
    
	REQUEST_FORBIDDEN(403, "requested resource is forbidden"),
	
	OPERATION_NOT_ALLOWED(406 , "Operation Not Allowed!."),
	
	NULL_POINTER_EXCEPTION(500, "Received Null response"), 
	
	SERVER_UNAVAILABLE(503, "Unable to Connect To the Server");
	

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
	
	public static HttpStatusCode  getHttpStatus(int statusCode) {
		for( HttpStatusCode httpStatusCode: HttpStatusCode.values()) {
			if(httpStatusCode.getCode() == statusCode) {
				return httpStatusCode;
			}
		}
		return null;
	}

	
}
