package com.javanoob.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class MliConfig {
	@Bean
	   public CorsFilter corsFilter() {
	       return new CorsFilter(request -> {
	           CorsConfiguration config = new CorsConfiguration();
	           config.setAllowedOrigins(Arrays.asList("*"));
	           config.setAllowedHeaders(Arrays.asList("*"));
	           config.setAllowCredentials(true);
	           config.setMaxAge(Long.valueOf(0));
	           config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
	           // , HEAD, , , PATCH, , OPTIONS, TRACE
	           config.setExposedHeaders(Arrays.asList("resultCode", "resultMsg"));
	           return config;
	       });
	   }
	}


