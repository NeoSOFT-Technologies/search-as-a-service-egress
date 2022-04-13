package com.searchservice.app;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.searchservice.app.rest.SearchResource;

@SpringBootTest
class SearchserviceApplicationTests {

	@Autowired
	SearchResource searchResource;
	
	@Test
	void contextLoads() {
		assertThat(searchResource).isNotNull();
	}

}
