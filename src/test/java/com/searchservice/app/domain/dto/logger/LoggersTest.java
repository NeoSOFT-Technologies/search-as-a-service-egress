package com.searchservice.app.domain.dto.logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

 class LoggersTest {

	Loggers l1=new Loggers();
	Loggers l2=new Loggers();
	
	Loggers l3=new Loggers("ab","cd","ef","gh","ij","kl");
	Loggers l4=new Loggers("mn","pq","rs","tu","vx","wz");
	
	@Test
	void testloggers()
	{ 
		String name="TestingEgress";
		l3.equals(l4);
		l3.hashCode();
		l3.toString();
	    l3.setCorrelationid("13");
		l3.setIpaddress("24");
		l3.setNameofmethod("Tester");
		l3.setServicename("TestingEgress");
		l3.setTimestamp("2hrs");
		l3.setUsername("User");
		l3.canEqual(l4);
		l3.getCorrelationid();
		l3.getIpaddress();
		l3.getNameofmethod();
		l3.getServicename();
		l3.getTimestamp();
		l3.getUsername();
		assertEquals(name,l3.getServicename());
		
	}
	
	@Test
	void testloggers2()
	{
		String address="2";
		l1.equals(l2);
		l1.hashCode();
		l1.toString();
	    l1.setCorrelationid("1");
		l1.setIpaddress("2");
		l1.setNameofmethod("Test");
		l1.setServicename("Testing");
		l1.setTimestamp("2h");
		l1.setUsername("Admin");
		l1.canEqual(l2);
		l1.getCorrelationid();
		l1.getIpaddress();
		l1.getNameofmethod();
		l1.getServicename();
		l1.getTimestamp();
		l1.getUsername();
		assertEquals(address,l1.getIpaddress());
	}
}
