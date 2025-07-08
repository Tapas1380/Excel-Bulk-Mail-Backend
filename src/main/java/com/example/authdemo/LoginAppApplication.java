package com.example.authdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class LoginAppApplication {

	public static void main(String[] args) {
	
		SpringApplication.run(LoginAppApplication.class, args);
	}
	 @Bean
	    public WebMvcConfigurer corsConfigurer() {
	        return new WebMvcConfigurer() {
	            @Override
	            public void addCorsMappings(CorsRegistry registry) {
	                registry.addMapping("/api/**")
	                        .allowedOrigins("http://localhost:4200")
	                        .allowedMethods("*")
	                        .allowedHeaders("*")
	                        .allowCredentials(true);
	            }
	        };
	    }

}
