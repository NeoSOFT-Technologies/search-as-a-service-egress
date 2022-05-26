package com.searchservice.app.rest.errors;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestApiError {

	private HttpStatus status;
	private int statusCode;
	private String message;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;

	private RestApiError() {
		timestamp = LocalDateTime.now();
	}

	static RestApiError getInstance() {
		return new RestApiError();
	}

	RestApiError(HttpStatus status, String message) {
		this();
		this.statusCode = status.value();
		this.status = status;
		this.message = message;
	}
	
	RestApiError(HttpStatus status) {
		this();
		this.status = status;
		this.message = "Unexpected Exception";
	}

	RestApiError(HttpStatus status, int statusCode, String message) {
		this();
		this.status = status;
		this.statusCode = statusCode;
		this.message = message;
	}

	RestApiError(HttpStatus status, Throwable ex) {
		this();
		this.status = status;
		this.message = ex.getLocalizedMessage();
	}
}