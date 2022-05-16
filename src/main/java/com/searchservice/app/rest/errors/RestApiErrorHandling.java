package com.searchservice.app.rest.errors;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.searchservice.app.domain.utils.HttpStatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestApiErrorHandling {

	private int statusCode;
	private String message;
	private HttpStatusCode status;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;

	private RestApiErrorHandling() {
		timestamp = LocalDateTime.now();
	}

	RestApiErrorHandling(int statuscode, HttpStatusCode status, String message) {
		this();
		this.statusCode = statuscode;
		this.status = status;
		this.message = message;
	}
}