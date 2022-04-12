package com.searchservice.app.rest.errors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;


public class RestApiErrorTest {

	RestApiError r1 = RestApiError.getInstance();	
	RestApiError r8 = RestApiError.getInstance();
	@MockBean
	HttpStatus status;

	@Test
	void testRestApiError()
	{
		r1.setMessage("Tester");
		r1.setStatus(null);
		r1.setStatusCode(101);
		r1.setTimestamp(null);
		r1.hashCode();
		r1.getMessage();
		r1.getStatus();
		r1.getStatusCode();
		r1.getTimestamp();
		r1.equals(r8);
		r1.canEqual(r8);
		r1.toString();
		
	}
	
	@Test 
	void testRestApiErrorHttpStatus()
	{
		RestApiError r2 = new RestApiError(status);
		RestApiError r7 = new RestApiError(status);
		r2.setStatusCode(200);
		r2.setStatus(status);
		r2.setTimestamp(null);
		r2.setMessage("Testerr");
		r2.hashCode();
		r2.getMessage();
		r2.getStatusCode();
		r2.getTimestamp();
		r2.equals(r7);
		r2.canEqual(r7);
		r2.toString();
	}
	
	@Test 
	void testRestApiErrorHttpStatusAndThrowable()
	{
		RestApiError r3 = new RestApiError(status, new Throwable("Test"));
		RestApiError r6 = new RestApiError(status, new Throwable("Test"));
		r3.setStatusCode(200);
		r3.setStatus(status);
		r3.setTimestamp(null);
		r3.setMessage("Testerr");
		r3.hashCode();
		r3.getMessage();
		r3.getStatusCode();
		r3.getTimestamp();
		r3.equals(r6);
		r3.canEqual(r6);
		r3.toString();
	}
	
	@Test 
	void testRestApiErrorHttpStatusMessageandStatusCode()
	{
		RestApiError r4 = new RestApiError(status,400,"Checking");
		RestApiError r5 = new RestApiError(status,400,"Checking");
		r4.setStatusCode(200);
		r4.setStatus(status);
		r4.setTimestamp(null);
		r4.setMessage("Testerr");
		r4.hashCode();
		r4.getMessage();
		r4.getStatusCode();
		r4.getTimestamp();
		r4.equals(r5);
		r4.canEqual(r5);
		r4.toString();
	}
}
