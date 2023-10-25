package com.app.kkiri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.app.kkiri.common.OAuth2AuthenticationFailureHandler;
import com.app.kkiri.common.OAuth2AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfig {
	// private final CustomOAuth2UserService customOAuth2UserService;
	// private final CustomOidcUserService customOidcUserService;

	private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Bean
	SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests(requests -> requests
			// .antMatchers("/").permitAll()
			// .antMatchers("/auth/**").permitAll()
			// .anyRequest().authenticated()
			//

			.antMatchers("/**").permitAll()
		);

		http.oauth2Login(oauth2 -> oauth2
			.successHandler(oAuth2AuthenticationSuccessHandler)
			.failureHandler(oAuth2AuthenticationFailureHandler));

		return http.build();
	}
}
