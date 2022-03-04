package com.searchservice.app.rest.errors;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

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
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception){
		String fieldName = "";
		if(exception.getCause() instanceof UnrecognizedPropertyException) {
			UnrecognizedPropertyException ex = (UnrecognizedPropertyException)exception.getCause();
			fieldName = ex.getPropertyName();
			return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, "Unrecognized Field : "+fieldName));
		}else if(exception.getCause() instanceof InvalidFormatException) {
			//String targetType = "";
			InvalidFormatException ex = (InvalidFormatException)exception.getCause();
			if (ex.getPath() != null && !ex.getPath().isEmpty()) {
		        JsonMappingException.Reference path = ex.getPath().get(ex.getPath().size() - 1);
		       fieldName = (null != path)?path.getFieldName():"";
		    }
			String value = (null != ex.getValue())?ex.getValue().toString():"";
			return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, "Value for field : "+fieldName+" is not expected as : "+value));
			//targetType = ex.getTargetType().getName();
		}else if(exception.getCause() instanceof JsonMappingException) {
			JsonMappingException ex = (JsonMappingException)exception.getCause();
			if(ex.getCause() instanceof BadRequestOccurredException) {
				BadRequestOccurredException exc = (BadRequestOccurredException)ex.getCause();
				return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, exc.getExceptionMessage()));
			}else
				return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, "Provide valid JSON Input"));
		}else {
			return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, "Provide valid JSON Input"));
		}
		//return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, "Unrecognized Field : "+fieldName));
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception){
		String fieldName = "";
		String requiredType = "";
		if(exception.getCause() instanceof NumberFormatException) {
			//NumberFormatException ex = (NumberFormatException)exception.getCause();
			fieldName = (null == exception.getName())?exception.getName():"";
			requiredType = (null == exception.getRequiredType())?exception.getRequiredType().getName():"";
		}
		return frameRestApiException(new RestApiError(HttpStatus.BAD_REQUEST, fieldName+" must be of type "+requiredType));
	}
}
