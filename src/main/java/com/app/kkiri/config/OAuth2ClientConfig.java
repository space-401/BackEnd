package com.app.kkiri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.app.kkiri.common.OAuth2AuthenticationFailureHandler;
import com.app.kkiri.common.OAuth2AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfig {
	// private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

	// private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

	@Bean
	SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests(requests -> requests
				.anyRequest().permitAll()
		);

//		http.cors().configurationSource(corsConfigurationSource());

		http.csrf().disable();

		http.oauth2Login(oauth2 -> oauth2
				.failureHandler(new SimpleUrlAuthenticationFailureHandler())
				.successHandler(new SimpleUrlAuthenticationSuccessHandler()));

		return http.build();
	}
}
