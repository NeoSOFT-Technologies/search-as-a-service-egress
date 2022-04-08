package com.searchservice.app.domain.dto.user;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.searchservice.app.domain.utils.GetCurrentSchemaUtil;

public class UserDTOTest {
	private final Logger logger = LoggerFactory.getLogger(UserDTO.class); 
	 @MockBean
	UserDTO userDTO=new UserDTO("testing","admin");
	UserDTO userDTO1=new UserDTO("no","123");
	UserDTO userDTO3=new UserDTO();
	UserDTO userDTO4=new UserDTO();
	@Test
	void testUserDTO()
	{
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
	}
	
}
