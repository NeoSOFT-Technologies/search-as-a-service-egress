package com.searchservice.app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.service.UserService;

 class UserServiceTest {
	UserService us1 =new UserService();
	
	@Test
	void userservicetest()
	{
		boolean value=false;
		UserService us2 =new UserService();
		us1.createResponse("password", "message", 200);
		us1.getToken("admin", "admin");
		assertEquals(value,us1.equals(us2));
	}
}