package com.searchservice.app.rest.errors;

import org.junit.jupiter.api.Test;

public class OperationNotAllowedExceptionTest {
	OperationNotAllowedException exp1=new OperationNotAllowedException();
	OperationNotAllowedException exp2=new OperationNotAllowedException();
	
	OperationNotAllowedException exp3=new OperationNotAllowedException(401,"Exp");
	OperationNotAllowedException exp4=new OperationNotAllowedException(401,"ExpOcc");
	@Test 
	void testOperationNotAllowedExceptionTestTest()
	{
		exp1.equals(exp2);
		exp1.hashCode();
		exp1.toString();
		exp1.getExceptionMessage();
		exp1.getExceptionCode();
		exp1.canEqual(exp2);
	}
	
	@Test 
	void testOperationNotAllowedExceptionTestTest1()
	{
		exp3.equals(exp4);
		exp3.hashCode();
		exp3.toString();
		exp3.setExceptionCode(400);
		exp3.setExceptionMessage("OperationNotAllowedExceptionTest");
	}
}
