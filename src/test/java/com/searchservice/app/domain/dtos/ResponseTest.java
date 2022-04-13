package com.searchservice.app.domain.dtos;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.searchservice.app.domain.dto.Response;

public class ResponseTest {
	Response res1=new Response("test");
	Response res2=new Response("test2");
	
	Response res3=new Response(200,"testing");
	Response res4=new Response(200,"testing");
	
	Response res5=new Response(200,"user","admin");
	Response res6=new Response(200,"user1","admin1");
	
	@Test
	void responsetest()
	{
		int code= 201;
		res1.getMessage();
		res1.equals(res2);
		res1.getToken();
		res1.toString();
		res1.hashCode();
		res1.setMessage("success");
		res1.setStatusCode(201);
		res1.setToken("true");
		assertEquals(code,res1.getStatusCode());
	}
	
	@Test
	void responsetest2()
	{
		int code= 202;
		res3.getMessage();
		res3.hashCode();
		res3.equals(res4);
		res3.getToken();
		res3.toString();
		res3.setMessage("success");
		res3.setStatusCode(202);
		res3.setMessage("True");
		assertEquals(code,res3.getStatusCode());
	}
	
	@Test 
	void responsetest3()
	{
		int code= 204;
		res5.getMessage();
		res5.hashCode();
		res5.equals(res6);
		res5.getToken();
		res5.toString();
		res5.setMessage("success");
		res5.setStatusCode(204);
		res5.setMessage("trUe");
		assertEquals(code,res5.getStatusCode());
	}
}
