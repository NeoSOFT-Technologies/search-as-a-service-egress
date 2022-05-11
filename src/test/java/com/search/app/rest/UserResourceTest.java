package com.search.app.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchservice.app.IntegrationTest;

import com.searchservice.app.domain.dto.Response;
import com.searchservice.app.domain.dto.user.UserDTO;
import com.searchservice.app.domain.service.UserService;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
public class UserResourceTest {

	ObjectMapper mapper = new ObjectMapper();
	@Autowired
	private MockMvc restMockMvc;
	
	@MockBean
	private UserService userService;
	
	public void setMockitoSuccessResponse() {
		Response tokenResponseDTO = new Response();
		tokenResponseDTO.setStatusCode(200);
		tokenResponseDTO.setMessage("Token Generated Sucessfully");	
		Mockito.when(userService.getToken(Mockito.any())).thenReturn(tokenResponseDTO);
	}
	
	public void setMockitoBadResponse() {
		Response tokenResponseDTO = new Response();
		tokenResponseDTO.setStatusCode(400);
		tokenResponseDTO.setMessage("Invalid Credentials");	
		Mockito.when(userService.getToken(Mockito.any())).thenReturn(tokenResponseDTO);
	}
	
	@Test
	void getTokenTest() throws Exception{
		
		UserDTO user = new UserDTO("test","123");
		setMockitoSuccessResponse();
		restMockMvc.perform(MockMvcRequestBuilders.post("/user/token")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(user)))
				.andExpect(status().isOk());
		
		setMockitoBadResponse();
		restMockMvc.perform(MockMvcRequestBuilders.post("/user/token")
				.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(user)))
				.andExpect(status().isBadRequest());
	}
}
