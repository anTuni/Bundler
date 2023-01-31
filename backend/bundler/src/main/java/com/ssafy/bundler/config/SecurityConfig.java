package com.ssafy.bundler.config;

import static org.springframework.security.config.Customizer.*;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ssafy.bundler.config.jwt.JwtAuthenticationFilter;
import com.ssafy.bundler.config.jwt.JwtAuthorizationFilter;
import com.ssafy.bundler.config.oauth.PrincipalOauth2UserService;
import com.ssafy.bundler.repository.UserRepository;

// https://github.com/spring-projects/spring-security/issues/10822 참고
@Configuration
@EnableWebSecurity // 시큐리티 활성화 -> 기본 스프링 필터체인에 등록
public class SecurityConfig {

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;

	@Autowired
	private UserRepository userRepository;

	// @Autowired
	// private CorsConfig corsConfig;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
			.cors(withDefaults())

			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.formLogin().disable()
			.httpBasic().disable()
			.apply(new MyCustomDsl()) // 커스텀 필터 등록
			.and()
			// .authorizeHttpRequests(authroize ->
			// 	authroize
			// 		.requestMatchers("/api/v1/auth/**").hasRole("ROLE_USER")
			// 		.requestMatchers("/api/v1/auth/manager/**").hasRole("ROLE_MANAGER")
			// 		.requestMatchers("/api/v1/auth/admin/**").hasRole("ROLE_ADMIN")
			// )
			.authorizeRequests(authroize -> //deprecated
				authroize.requestMatchers("/api/v1/auth/user/**")
					.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
					.requestMatchers("/api/v1/auth/manager/**")
					.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
					.requestMatchers("/api/v1/auth/admin/**")
					.access("hasRole('ROLE_ADMIN')")
					.anyRequest().permitAll()
			)
			// .and()
			// .oauth2Login()
			// .loginPage("/login")
			// .userInfoEndpoint()
			// .userService(principalOauth2UserService)
			.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(List.of("*"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
			http
				// .addFilter(corsConfig.corsFilter())
				.addFilter(new JwtAuthenticationFilter(authenticationManager))
				.addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository));
		}
	}

}
