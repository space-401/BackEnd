package com.app.kkiri.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthorizationRequestResolver {

	private static final StringKeyGenerator DEFAULT_STATE_GENERATOR = new Base64StringKeyGenerator(
		Base64.getUrlEncoder());

	private final ClientRegistrationRepository clientRegistrationRepository;

	private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer = (customizer) -> {
	};

	public OAuth2AuthorizationRequest resolve(String registrationId, String state) {
		if (registrationId == null) {
			return null;
		}

		// 인가서버로부터 인가 코드 받기 응답이 실패한 경우 state 는 null 이지만
		// 온전한 OAuth2AuthorizationRequest 를 생성하기 위해 임의의 문자열을 저장한다.
		if(state == null) {
			state = DEFAULT_STATE_GENERATOR.generateKey();
		}

		String redirectUriAction = "login";

		ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
		if (clientRegistration == null) {
			throw new IllegalArgumentException("Invalid Client Registration with Id: " + registrationId);
		}
		OAuth2AuthorizationRequest.Builder builder = getBuilder(clientRegistration);

		String redirectUriStr = UriComponentsBuilder
			.fromUriString("http://52.79.57.246:8081")
			.path("/user/auth/" + registrationId)
			.build()
			.toUriString();

		// @formatter:off
		builder.clientId(clientRegistration.getClientId())
			.authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
			.redirectUri(redirectUriStr)
			.scopes(clientRegistration.getScopes())
			.state(state);
		// @formatter:on

		this.authorizationRequestCustomizer.accept(builder);

		return builder.build();
	}

	private OAuth2AuthorizationRequest.Builder getBuilder(ClientRegistration clientRegistration) {
		if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(clientRegistration.getAuthorizationGrantType())) {
			// @formatter:off
			OAuth2AuthorizationRequest.Builder builder = OAuth2AuthorizationRequest.authorizationCode()
				.attributes((attrs) ->
					attrs.put(OAuth2ParameterNames.REGISTRATION_ID, clientRegistration.getRegistrationId()));
			// @formatter:on
			return builder;
		}
		throw new IllegalArgumentException(
			"Invalid Authorization Grant Type (" + clientRegistration.getAuthorizationGrantType().getValue()
				+ ") for Client Registration with Id: " + clientRegistration.getRegistrationId());
	}
}
