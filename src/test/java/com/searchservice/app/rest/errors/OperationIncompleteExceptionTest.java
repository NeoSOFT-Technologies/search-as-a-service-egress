package com.searchservice.app.rest.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class OperationIncompleteExceptionTest {

	OperationIncompleteException exp1=new OperationIncompleteException();
	OperationIncompleteException exp2=new OperationIncompleteException();
	
	OperationIncompleteException exp3=new OperationIncompleteException(401,"Exp");
	OperationIncompleteException exp4=new OperationIncompleteException(401,"ExpOcc");
	@Test 
	void testOperationIncompleteExceptionTest()
	{
		int code=400;
		exp1.equals(exp2);
		exp1.hashCode();
		exp1.toString();
		exp1.setExceptionCode(400);
		exp1.getExceptionMessage();
		exp1.getExceptionCode();
		exp1.canEqual(exp2);
		assertEquals(code,exp1.getExceptionCode());
		
	}
	
	@Test 
	void testOperationIncompleteExceptionTest1()
	{
		int code=400;
		exp3.equals(exp4);
		exp3.hashCode();
		exp3.toString();
		exp3.setExceptionCode(400);
		exp3.setExceptionMessage("OperationIncompleteException");
		assertEquals(code,exp3.getExceptionCode());
	}
}
