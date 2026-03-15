package com.bearcat.store_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the store API.
 * This class defines the password encoder and the basic security filter chain.
 */
@Configuration
public class ProjectSecurityConfig {

    /**
     * Creates the password encoder used to store user passwords securely.
     *
     * @return the application's password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Builds the application's security filter chain.
     *
     * @param http the HttpSecurity configuration
     * @return the configured security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.build();
    }

}
