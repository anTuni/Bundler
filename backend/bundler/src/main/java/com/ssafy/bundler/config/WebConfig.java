package com.ssafy.bundler.config;// package com.ssafy.bundler.config;
//
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
// @Configuration
// public class WebConfig implements WebMvcConfigurer {
//
// 	private final long MAX_AGE_SECONDS = 3600;
//
// 	@Override
// 	public void addCorsMappings(CorsRegistry registry) {
// 		registry.addMapping("/**")
// 			.allowedOrigins("http://localhost:3000")
// 			.allowedMethods("*")
// 			.allowedHeaders("*")
// 			.allowCredentials(true)
// 			.maxAge(MAX_AGE_SECONDS);
// 	}
//
// }