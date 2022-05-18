package com.searchservice.app.config.security;

import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.client.RestTemplate;

import com.searchservice.app.domain.filter.JwtTokenFilterService;
import com.searchservice.app.domain.utils.KeycloakConfigProperties;

@KeycloakConfiguration
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(jsr250Enabled = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	KeycloakConfigProperties keycloakConfigProperties;

	@Value("${keycloak.realm}")
	private String realmName;

	@Value("${keycloak.resource}")
	private String clientId;

	@Value("${keycloak.credentials.secret}")
	private String clientSecret;
	
	@Value("${base-url.api-endpoint.home}")
	private String baseHomeUrl;

	// Register Keycloak as the Authentication Provider
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) {

		KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
		keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
		auth.authenticationProvider(keycloakAuthenticationProvider);
	}

	// Defines the session authentication strategy.
	@Bean
	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new NullAuthenticatedSessionStrategy();
	}

	@Bean
	@Override
	@ConditionalOnMissingBean(HttpSessionManager.class)
	protected HttpSessionManager httpSessionManager() {
		return new HttpSessionManager();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/user/token").antMatchers("/v3/api-docs/**").antMatchers("/swagger-ui/**")
				.antMatchers("/test/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.headers().frameOptions().sameOrigin();
        
        // Set session management to stateless
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = http.cors()
        .and()
        .csrf().disable()	// Disable CSRF
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests();	

        expressionInterceptUrlRegistry.antMatchers(HttpMethod.GET, baseHomeUrl+"/**").hasAnyRole("user", "search_admin")
        .antMatchers(baseHomeUrl+"/**").hasAnyRole("search_admin", "admin");
        
        expressionInterceptUrlRegistry.anyRequest().permitAll();
		
		// Add JWT token filter
		http.addFilterBefore(new JwtTokenFilterService(keycloakConfigProperties, restTemplate),
				UsernamePasswordAuthenticationFilter.class);
	}
}
