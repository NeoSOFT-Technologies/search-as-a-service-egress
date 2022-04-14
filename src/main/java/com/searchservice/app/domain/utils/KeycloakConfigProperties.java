package com.searchservice.app.domain.utils;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakConfigProperties {

    private Credentials credentials;
    private String realm;
    private String resource;
    private String authServerUrl;
    private String sslRequired;
    private String publicClient;
    private String useResourceRoleMappings;
    @Getter
    @Setter
    public static class Credentials {
        private String secret;
    }

}
