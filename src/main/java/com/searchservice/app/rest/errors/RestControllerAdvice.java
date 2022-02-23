package com.searchservice.app.rest.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestControllerAdvice {

	  private final Logger log = LoggerFactory.getLogger(RestControllerAdvice.class);
	  
	@ExceptionHandler(BadRequestOccurredException.class)
	public ResponseEntity<Object> handleBadRequestOccurred(
			BadRequestOccurredException exception) {
		return frameRestApiException(new RestApiError(
										HttpStatus.BAD_REQUEST, 
										exception.getExceptionMessage()));
	}
	
	
	@ExceptionHandler(OperationNotAllowedException.class)
	public ResponseEntity<Object> handleOperationNotAllowed(
			OperationNotAllowedException exception) {
		return frameRestApiException(new RestApiError(
										HttpStatus.NOT_ACCEPTABLE, 
										exception.getExceptionMessage()));
	}
	
	
	@ExceptionHandler(NullPointerOccurredException.class)
	public ResponseEntity<Object> handleNullPointerOccurredException(
			NullPointerOccurredException exception) {
		return frameRestApiException(new RestApiError(
										HttpStatus.NOT_FOUND, 
										exception.getExceptionMessage()));
	}
	

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUncaughtException(
			Exception exception) {
		log.error("Uncaught Error Occured: {}", exception.getMessage());
		return frameRestApiException(new RestApiError(
										HttpStatus.BAD_REQUEST, 
										"Uncaught error occurred. "+exception.getMessage()));
	}
	
	
	private ResponseEntity<Object> frameRestApiException(RestApiError err) {
		return new ResponseEntity<>(err, err.getStatus());
	}
}
