package com.app.kkiri.security.oAuth2Login;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public final class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

	private static final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";

	private static final char PATH_DELIMITER = '/';

	// private static final StringKeyGenerator DEFAULT_STATE_GENERATOR = new Base64StringKeyGenerator(
	// 	Base64.getUrlEncoder());

	private static final StringKeyGenerator DEFAULT_SECURE_KEY_GENERATOR = new Base64StringKeyGenerator(
		Base64.getUrlEncoder().withoutPadding(), 96);

	private static final Consumer<OAuth2AuthorizationRequest.Builder> DEFAULT_PKCE_APPLIER = OAuth2AuthorizationRequestCustomizers
		.withPkce();

	private final ClientRegistrationRepository clientRegistrationRepository;

	private final AntPathRequestMatcher authorizationRequestMatcher;

	private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer = (customizer) -> {
	};

	/**
	 * Constructs a {@code DefaultOAuth2AuthorizationRequestResolver} using the provided
	 * parameters.
	 * @param clientRegistrationRepository the repository of client registrations
	 * @param authorizationRequestBaseUri the base {@code URI} used for resolving
	 * authorization requests
	 */
	public CustomOAuth2AuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
		String authorizationRequestBaseUri) {
		Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
		Assert.hasText(authorizationRequestBaseUri, "authorizationRequestBaseUri cannot be empty");
		this.clientRegistrationRepository = clientRegistrationRepository;
		this.authorizationRequestMatcher = new AntPathRequestMatcher(
			authorizationRequestBaseUri + "/{" + REGISTRATION_ID_URI_VARIABLE_NAME + "}");
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		String registrationId = resolveRegistrationId(request);
		if (registrationId == null) {
			return null;
		}
		String redirectUriAction = getAction(request, "login");
		return resolve(request, registrationId, redirectUriAction);
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId) {
		if (registrationId == null) {
			return null;
		}
		String redirectUriAction = getAction(request, "authorize");
		return resolve(request, registrationId, redirectUriAction);
	}

	/**
	 * Sets the {@code Consumer} to be provided the
	 * {@link OAuth2AuthorizationRequest.Builder} allowing for further customizations.
	 * @param authorizationRequestCustomizer the {@code Consumer} to be provided the
	 * {@link OAuth2AuthorizationRequest.Builder}
	 * @since 5.3
	 * @see OAuth2AuthorizationRequestCustomizers
	 */
	public void setAuthorizationRequestCustomizer(
		Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer) {
		Assert.notNull(authorizationRequestCustomizer, "authorizationRequestCustomizer cannot be null");
		this.authorizationRequestCustomizer = authorizationRequestCustomizer;
	}

	private String getAction(HttpServletRequest request, String defaultAction) {
		String action = request.getParameter("action");
		if (action == null) {
			return defaultAction;
		}
		return action;
	}

	private OAuth2AuthorizationRequest resolve(HttpServletRequest request, String registrationId,
		String redirectUriAction) {
		if (registrationId == null) {
			return null;
		}
		ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
		if (clientRegistration == null) {
			throw new IllegalArgumentException("Invalid Client Registration with Id: " + registrationId);
		}
		OAuth2AuthorizationRequest.Builder builder = getBuilder(clientRegistration);

		// String redirectUriStr = expandRedirectUri(request, clientRegistration, redirectUriAction);
		String redirectUriStr = clientRegistration.getRedirectUri();

		// @formatter:off
		builder.clientId(clientRegistration.getClientId())
			.authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
			.redirectUri(redirectUriStr)
			.scopes(clientRegistration.getScopes())

			//
			.state(request.getParameter("state"));
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
			if (!CollectionUtils.isEmpty(clientRegistration.getScopes())
				&& clientRegistration.getScopes().contains(OidcScopes.OPENID)) {
				// Section 3.1.2.1 Authentication Request -
				// https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest scope
				// REQUIRED. OpenID Connect requests MUST contain the "openid" scope
				// value.
				applyNonce(builder);
			}
			if (ClientAuthenticationMethod.NONE.equals(clientRegistration.getClientAuthenticationMethod())) {
				DEFAULT_PKCE_APPLIER.accept(builder);
			}
			return builder;
		}
		if (AuthorizationGrantType.IMPLICIT.equals(clientRegistration.getAuthorizationGrantType())) {
			return OAuth2AuthorizationRequest.implicit();
		}
		throw new IllegalArgumentException(
			"Invalid Authorization Grant Type (" + clientRegistration.getAuthorizationGrantType().getValue()
				+ ") for Client Registration with Id: " + clientRegistration.getRegistrationId());
	}

	private String resolveRegistrationId(HttpServletRequest request) {
		if (this.authorizationRequestMatcher.matches(request)) {
			return this.authorizationRequestMatcher.matcher(request).getVariables()
				.get(REGISTRATION_ID_URI_VARIABLE_NAME);
		}
		return null;
	}

	/**
	 * Creates nonce and its hash for use in OpenID Connect 1.0 Authentication Requests.
	 * @param builder where the {@link OidcParameterNames#NONCE} and hash is stored for
	 * the authentication request
	 *
	 * @since 5.2
	 * @see <a target="_blank" href=
	 * "https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest">3.1.2.1.
	 * Authentication Request</a>
	 */
	private static void applyNonce(OAuth2AuthorizationRequest.Builder builder) {
		try {
			String nonce = DEFAULT_SECURE_KEY_GENERATOR.generateKey();
			String nonceHash = createHash(nonce);
			builder.attributes((attrs) -> attrs.put(OidcParameterNames.NONCE, nonce));
			builder.additionalParameters((params) -> params.put(OidcParameterNames.NONCE, nonceHash));
		}
		catch (NoSuchAlgorithmException ex) {
		}
	}

	private static String createHash(String value) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] digest = md.digest(value.getBytes(StandardCharsets.US_ASCII));
		return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
	}
}
