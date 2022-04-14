package com.searchservice.app.domain.dto.logger;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

 class CorrelationIDTest {
  
	CorrelationID c1=new CorrelationID();
	CorrelationID c2=new CorrelationID();
	
	@Test 
	void testCorrelationID()
	{
		c1.hashCode();
		c1.toString();
		c1.equals(c2);
        assertThat(c1.generateUniqueCorrelationId()).isNotNull();

	}
	
}
