package com.letslive.letslearnbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(se -> se.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> {
                    a.requestMatchers("/course/**").authenticated();
                    a.requestMatchers("/user/**").authenticated();
                    a.requestMatchers("/section/**").authenticated();
                    a.requestMatchers("/topic/**").authenticated();
                    a.requestMatchers("/question/**").authenticated();
                    a.requestMatchers("/auth/logout").permitAll();
                    a.requestMatchers("/v3/api-docs/**").permitAll();
                    a.requestMatchers("/swagger-ui/**").permitAll();
                    a.requestMatchers("/swagger-ui.html").permitAll();
                    a.requestMatchers("/ws/**").permitAll();
                    a.anyRequest().permitAll();
                })
                .addFilterAfter(jwtAuthFilter, SessionManagementFilter.class)
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .build();
    }
}
