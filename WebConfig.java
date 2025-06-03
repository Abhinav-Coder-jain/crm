package com.example.crm; // Adjust package name as needed

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Temporarily allow all origins during initial setup, but REMEMBER TO CHANGE THIS
        // to your actual Vercel frontend URL for security later!
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8081", "https://*.vercel.app") // Include localhost for dev, and a wildcard for Vercel
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}