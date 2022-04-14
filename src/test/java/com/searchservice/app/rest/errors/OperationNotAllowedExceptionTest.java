package com.searchservice.app.rest.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class OperationNotAllowedExceptionTest {
	OperationNotAllowedException exp1=new OperationNotAllowedException();
	OperationNotAllowedException exp2=new OperationNotAllowedException();
	
	OperationNotAllowedException exp3=new OperationNotAllowedException(401,"Exp");
	OperationNotAllowedException exp4=new OperationNotAllowedException(401,"ExpOcc");
	@Test 
	void testOperationNotAllowedExceptionTestTest()
	{
		String message="Exp Occurred";
		exp1.equals(exp2);
		exp1.hashCode();
		exp1.toString();
		exp1.setExceptionMessage("Exp Occurred");
		exp1.getExceptionMessage();
		exp1.getExceptionCode();
		exp1.canEqual(exp2);
		assertEquals(message,exp1.getExceptionMessage());
	}
	
	@Test 
	void testOperationNotAllowedExceptionTestTest1()
	{
		int code=400;
		exp3.equals(exp4);
		exp3.hashCode();
		exp3.toString();
		exp3.setExceptionCode(400);
		exp3.setExceptionMessage("OperationNotAllowedExceptionTest");
		assertEquals(code,exp3.getExceptionCode());
	}
}
