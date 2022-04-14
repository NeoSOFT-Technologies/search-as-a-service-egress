package com.searchservice.app.rest.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class NullPointerOccurredExceptionTest {
	
	NullPointerOccurredException exp1=new NullPointerOccurredException();
	NullPointerOccurredException exp2=new NullPointerOccurredException();
	
	NullPointerOccurredException exp3=new NullPointerOccurredException(401,"ExpOccurred");
	NullPointerOccurredException exp4=new NullPointerOccurredException(401,"ExpOccurred");
	@Test 
	void testNullPointerOccurredException()
	{ 
		int code =400;
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
	void testNullPointerOccurredException1()
	{
		int code=400;
		exp3.equals(exp4);
		exp3.hashCode();
		exp3.toString();
		exp3.setExceptionCode(400);
		exp3.setExceptionMessage("NULLPOINTEREXCEPTION");
		assertEquals(code,exp3.getExceptionCode());
	}
	
}
