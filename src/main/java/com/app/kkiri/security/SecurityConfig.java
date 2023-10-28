package com.app.kkiri.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.kkiri.security.handlers.OAuth2AuthenticationFailureHandler;
import com.app.kkiri.security.handlers.OAuth2AuthenticationSuccessHandler;
import com.app.kkiri.security.service.CustomOAuth2UserService;
import com.app.kkiri.security.utils.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomOAuth2UserService customOAuth2UserService;

	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().configurationSource(corsConfigurationSource());

		http.csrf().disable();

		http.authorizeHttpRequests().anyRequest().permitAll();

		// http.authorizeRequests(requests -> requests
		// 	.antMatchers("/").permitAll()
		// 	.anyRequest().authenticated()
		// 	.and()
		// 	.exceptionHandling().authenticationEntryPoint()
		// );

		// http.addFilterAfter(new JwtAuthenticationFilter(jwtTokenProvider), LogoutFilter.class);

		// http.oauth2Login(oauth2 -> oauth2
		// 	.userInfoEndpoint(userInfo -> userInfo
		// 		.userService(customOAuth2UserService))
		// 	.successHandler(oAuth2AuthenticationSuccessHandler)
		// 	.failureHandler(oAuth2AuthenticationFailureHandler));

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
