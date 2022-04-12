package com.searchservice.app;

import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.service.UserService;

public class UserServiceTest {
	UserService us1 =new UserService();
	
	@Test
	void userservicetest()
	{
		us1.createResponse("password", "message", 200);
		us1.getToken("admin", "admin");
	}
}
