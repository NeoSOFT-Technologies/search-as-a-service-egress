package com.searchservice.app.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.searchservice.app.domain.filter.JwtTokenAuthorizationFilter;
import com.searchservice.app.domain.service.PublicKeyService;
import com.searchservice.app.domain.service.security.KeycloakPermissionManagementService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

	@Autowired
	private PublicKeyService publicKeyService;

	@Autowired
	private KeycloakPermissionManagementService kpmService;
	
    @Override
	public void configure(WebSecurity web) throws Exception {
    	web.ignoring().antMatchers("/user/token").antMatchers("/v3/api-docs/**").antMatchers("/swagger-ui/**").antMatchers("/test/**");
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF
        http = http.csrf().disable();

        // Set session management to stateless
        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();
        
		// Add JWT token filter
		http.addFilterBefore(new JwtTokenAuthorizationFilter(kpmService, publicKeyService),
				UsernamePasswordAuthenticationFilter.class);

    }
}