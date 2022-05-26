package com.searchservice.app.domain.dto.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

 class UserTest {
	private final Logger logger = LoggerFactory.getLogger(User.class); 
	 @MockBean
	User userDTO=new User("testing","admin");
	User userDTO1=new User("no","123");
	User userDTO3=new User();
	User userDTO4=new User();
	@Test
	void testUserDTO()
	{
		String password="admin";
		userDTO.setPassword("admin");
		userDTO.setUsername("testing");
		logger.info("Testing: "+userDTO.getPassword());
		logger.info("Testing: "+userDTO.getUsername());
		logger.info("Testing: "+userDTO.toString());;
		userDTO.hashCode();
		userDTO1.hashCode();
		userDTO3.hashCode();
		userDTO4.hashCode();
		userDTO.equals(userDTO1);
		userDTO3.equals(userDTO4);
		assertEquals(password,userDTO.getPassword());
	}
	
}
