package com.searchservice.app.rest.errors;

import org.junit.jupiter.api.Test;

public class NullPointerOccurredExceptionTest {
	
	NullPointerOccurredException exp1=new NullPointerOccurredException();
	NullPointerOccurredException exp2=new NullPointerOccurredException();
	
	NullPointerOccurredException exp3=new NullPointerOccurredException(401,"ExpOccurred");
	NullPointerOccurredException exp4=new NullPointerOccurredException(401,"ExpOccurred");
	@Test 
	void testNullPointerOccurredException()
	{
		exp1.equals(exp2);
		exp1.hashCode();
		exp1.toString();
		exp1.getExceptionMessage();
		exp1.getExceptionCode();
		exp1.canEqual(exp2);
	}
	
	@Test 
	void testNullPointerOccurredException1()
	{
		exp3.equals(exp4);
		exp3.hashCode();
		exp3.toString();
		exp3.setExceptionCode(400);
		exp3.setExceptionMessage("NULLPOINTEREXCEPTION");
	}
	
}
