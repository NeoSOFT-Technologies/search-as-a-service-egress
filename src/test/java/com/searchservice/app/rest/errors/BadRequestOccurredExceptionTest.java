package com.searchservice.app.rest.errors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class BadRequestOccurredExceptionTest {

	BadRequestOccurredException exp1=new BadRequestOccurredException();
	BadRequestOccurredException exp2=new BadRequestOccurredException();
	
	BadRequestOccurredException exp3=new BadRequestOccurredException(400,"BADRES");
	BadRequestOccurredException exp4=new BadRequestOccurredException(400,"BADRESQ");
	@Test
	void testbadRequestOccurredException()
	{ 
		int code=400;
		exp1.equals(exp2);
		exp1.hashCode();
		exp1.toString();
		exp1.setExceptionCode(400);
		exp1.setExceptionMessage("BAD_REQUEST");
		exp1.getExceptionCode();
		exp1.getExceptionMessage();
		assertEquals(code,exp1.getExceptionCode());
	}
	
	@Test
	void testbadRequestOccurredException1()
	{
		int code =401;
		exp3.equals(exp4);
		exp3.hashCode();
		exp3.toString();
		exp3.setExceptionCode(401);
		exp3.setExceptionMessage("BAD_REQUEST");
		exp3.getExceptionCode();
		exp3.getExceptionMessage();
		assertEquals(code,exp3.getExceptionCode());
	}
}
