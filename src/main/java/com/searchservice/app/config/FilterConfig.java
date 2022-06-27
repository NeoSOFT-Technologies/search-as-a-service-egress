package com.searchservice.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.searchservice.app.domain.filter.ResourcesAuthorizationFilter;

@Configuration
public class FilterConfig {
	
	@Autowired
	ResourcesAuthorizationFilter resourcesAuthorizationFilter;
	
	@Bean
	public FilterRegistrationBean<ResourcesAuthorizationFilter> registrationBeanResourcesAuthorization() {
		
		FilterRegistrationBean<ResourcesAuthorizationFilter> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(resourcesAuthorizationFilter);
		registrationBean.addUrlPatterns("/search/api/v1/*");
		
		return registrationBean;
	}
}
