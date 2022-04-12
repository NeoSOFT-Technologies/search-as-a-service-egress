package com.searchservice.app.domain.dto.logger;

import org.junit.jupiter.api.Test;

public class CorrelationIDTest {
  
	CorrelationID c1=new CorrelationID();
	CorrelationID c2=new CorrelationID();
	@Test 
	void testCorrelationID()
	{
		c1.hashCode();
		c1.toString();
		c1.equals(c2);
		c1.generateUniqueCorrelationId();
	}
	
}
