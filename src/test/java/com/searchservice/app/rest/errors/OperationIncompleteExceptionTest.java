package com.searchservice.app.rest.errors;

import org.junit.jupiter.api.Test;

public class OperationIncompleteExceptionTest {

	OperationIncompleteException exp1=new OperationIncompleteException();
	OperationIncompleteException exp2=new OperationIncompleteException();
	
	OperationIncompleteException exp3=new OperationIncompleteException(401,"Exp");
	OperationIncompleteException exp4=new OperationIncompleteException(401,"ExpOcc");
	@Test 
	void testOperationIncompleteExceptionTest()
	{
		exp1.equals(exp2);
		exp1.hashCode();
		exp1.toString();
		exp1.getExceptionMessage();
		exp1.getExceptionCode();
		exp1.canEqual(exp2);
	}
	
	@Test 
	void testOperationIncompleteExceptionTest1()
	{
		exp3.equals(exp4);
		exp3.hashCode();
		exp3.toString();
		exp3.setExceptionCode(400);
		exp3.setExceptionMessage("OperationIncompleteException");
	}
}
