package com.app.kkiri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.kkiri.common.OAuth2AuthenticationFailureHandler;
import com.app.kkiri.common.OAuth2AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Bean
	SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests(requests -> requests
			.anyRequest().permitAll()
		);

		http.cors().configurationSource(corsConfigurationSource());

		http.csrf().disable();

		http.oauth2Login(oauth2 -> oauth2
			.successHandler(oAuth2AuthenticationSuccessHandler)
			.failureHandler(oAuth2AuthenticationFailureHandler));

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		// 주어진 요청의 실제 출처, HTTP 메소드 및 헤더를 확인하는 메소드와 함께 CORS 구성을 위한 컨테이너입니다.
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.addAllowedHeader("*");

		// setAllowCredentials(true) 로 작성할 경우
		// addAllowOrigin() 에 "*" 를 작성할 수 없다.
		// corsConfiguration.setAllowCredentials(true);

		// URL 경로 패턴을 사용하여 요청에 대한 CorsConfiguration 을 선택하는 CorsConfigurationSource 입니다.
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		// 모든 요청에 대해 corsConfiguration 을 매핑한다.
		source.registerCorsConfiguration("/**", corsConfiguration);

		return source;
	}
}
