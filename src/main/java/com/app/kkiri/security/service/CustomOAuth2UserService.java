package com.app.kkiri.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.app.kkiri.security.converters.CustomOAuth2UserConverter;
import com.app.kkiri.security.model.AuthenticatedUser;
import com.app.kkiri.security.model.ProviderUserRequest;
import com.app.kkiri.security.model.CustomOAuth2User;
import com.app.kkiri.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private final Logger LOGGER = LoggerFactory.getLogger(CustomOAuth2UserService.class);

	private final CustomOAuth2UserConverter<ProviderUserRequest, CustomOAuth2User> customOAuth2UserConverter;

	private final UserService userService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		LOGGER.info("[loadUser()] userRequest : {}", userRequest);

		ClientRegistration clientRegistration = userRequest.getClientRegistration();

		OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
		LOGGER.info("[loadUser()] oAuth2User : {}", oAuth2User);

		ProviderUserRequest providerUserRequest = new ProviderUserRequest(clientRegistration, oAuth2User);

		CustomOAuth2User customOAuth2User = customOAuth2UserConverter.convert(providerUserRequest);
		LOGGER.info("[loadUser()] returned value customOAuth2User : {}", customOAuth2User);

		AuthenticatedUser authenticatedUser = userService.register(customOAuth2User);
		LOGGER.info("[loadUser()] returned value authenticatedUser : {}", authenticatedUser);

		return authenticatedUser;
	}
}





















