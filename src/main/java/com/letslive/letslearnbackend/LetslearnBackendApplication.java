package com.letslive.letslearnbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class LetslearnBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LetslearnBackendApplication.class, args);
    }

    //@Bean
    //public WebMvcConfigurer corsConfigurer() {
    //    return new WebMvcConfigurer() {
    //        @Override
    //        public void addCorsMappings(CorsRegistry registry) {
    //            registry.addMapping("/**") // Use "/**" to apply to all endpoints
    //                    .allowedOriginPatterns("*")
    //                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Allowed HTTP methods
    //                    .allowedHeaders("*") // Allow all headers
    //                    .maxAge(3600)
    //                    .allowCredentials(true); // Allow credentials (cookies, authorization headers)
    //        }
    //    };
    //}

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //configuration.setAllowedOrigins(List.of("localhost:5000"));
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
