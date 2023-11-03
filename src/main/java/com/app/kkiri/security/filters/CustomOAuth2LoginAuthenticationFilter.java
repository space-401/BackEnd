package com.app.kkiri.security.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.app.kkiri.security.utils.CustomOAuth2AuthorizationRequestResolver;
import com.app.kkiri.security.utils.CustomOAuth2AuthorizationResponseUtils;

public class CustomOAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public static final String DEFAULT_FILTER_PROCESSES_URI = "/user/auth/*";

	private static final String AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE = "authorization_request_not_found";

	private static final String CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE = "client_registration_not_found";

	private static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI = "/user/auth";

	//
	private OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver;

	private ClientRegistrationRepository clientRegistrationRepository;

	private OAuth2AuthorizedClientRepository authorizedClientRepository;

	private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();

	private Converter<OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken> authenticationResultConverter = this::createAuthenticationResult;

	public CustomOAuth2LoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
		OAuth2AuthorizedClientService authorizedClientService) {
		this(clientRegistrationRepository, authorizedClientService, DEFAULT_FILTER_PROCESSES_URI);
	}

	public CustomOAuth2LoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
		OAuth2AuthorizedClientService authorizedClientService, String filterProcessesUrl) {
		this(clientRegistrationRepository, new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService),
			filterProcessesUrl);
	}

	public CustomOAuth2LoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
		OAuth2AuthorizedClientRepository authorizedClientRepository, String filterProcessesUrl) {
		this(clientRegistrationRepository, authorizedClientRepository, filterProcessesUrl,
			new CustomOAuth2AuthorizationRequestResolver(clientRegistrationRepository, CustomOAuth2LoginAuthenticationFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI));
	}

	public CustomOAuth2LoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository,
		OAuth2AuthorizedClientRepository authorizedClientRepository, String filterProcessesUrl,
		OAuth2AuthorizationRequestResolver oAuth2AuthorizationRequestResolver) {
		super(filterProcessesUrl);
		Assert.notNull(clientRegistrationRepository, "clientRegistrationRepository cannot be null");
		Assert.notNull(authorizedClientRepository, "authorizedClientRepository cannot be null");
		Assert.notNull(oAuth2AuthorizationRequestResolver, "oAuth2AuthorizationRequestResolver cannot be null");
		this.clientRegistrationRepository = clientRegistrationRepository;
		this.authorizedClientRepository = authorizedClientRepository;
		this.oAuth2AuthorizationRequestResolver = oAuth2AuthorizationRequestResolver;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException {

		//
		if(this.oAuth2AuthorizationRequestResolver != null) {
			OAuth2AuthorizationRequest oAuth2AuthorizationRequest = this.oAuth2AuthorizationRequestResolver.resolve(request);
			this.authorizationRequestRepository.saveAuthorizationRequest(oAuth2AuthorizationRequest, request, response);
		}

		MultiValueMap<String, String> params = CustomOAuth2AuthorizationResponseUtils.toMultiMap(request.getParameterMap());
		if (!CustomOAuth2AuthorizationResponseUtils.isAuthorizationResponse(params)) {
			OAuth2Error oauth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
		}
		OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository
			.removeAuthorizationRequest(request, response);
		if (authorizationRequest == null) {
			OAuth2Error oauth2Error = new OAuth2Error(AUTHORIZATION_REQUEST_NOT_FOUND_ERROR_CODE);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
		}
		String registrationId = authorizationRequest.getAttribute(OAuth2ParameterNames.REGISTRATION_ID);
		ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(registrationId);
		if (clientRegistration == null) {
			OAuth2Error oauth2Error = new OAuth2Error(CLIENT_REGISTRATION_NOT_FOUND_ERROR_CODE,
				"Client Registration not found with Id: " + registrationId, null);
			throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
		}
		// @formatter:off
		String redirectUri = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
			.replaceQuery(null)
			.build()
			.toUriString();
		// @formatter:on
		OAuth2AuthorizationResponse authorizationResponse = CustomOAuth2AuthorizationResponseUtils.convert(params,
			redirectUri);
		Object authenticationDetails = this.authenticationDetailsSource.buildDetails(request);
		OAuth2LoginAuthenticationToken authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration,
			new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse));
		authenticationRequest.setDetails(authenticationDetails);
		OAuth2LoginAuthenticationToken authenticationResult = (OAuth2LoginAuthenticationToken) this
			.getAuthenticationManager().authenticate(authenticationRequest);
		OAuth2AuthenticationToken oauth2Authentication = this.authenticationResultConverter
			.convert(authenticationResult);
		Assert.notNull(oauth2Authentication, "authentication result cannot be null");
		oauth2Authentication.setDetails(authenticationDetails);
		OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
			authenticationResult.getClientRegistration(), oauth2Authentication.getName(),
			authenticationResult.getAccessToken(), authenticationResult.getRefreshToken());

		this.authorizedClientRepository.saveAuthorizedClient(authorizedClient, oauth2Authentication, request, response);
		return oauth2Authentication;
	}

	public final void setAuthorizationRequestRepository(
		AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
		Assert.notNull(authorizationRequestRepository, "authorizationRequestRepository cannot be null");
		this.authorizationRequestRepository = authorizationRequestRepository;
	}

	public final void setAuthenticationResultConverter(
		Converter<OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken> authenticationResultConverter) {
		Assert.notNull(authenticationResultConverter, "authenticationResultConverter cannot be null");
		this.authenticationResultConverter = authenticationResultConverter;
	}

	private OAuth2AuthenticationToken createAuthenticationResult(OAuth2LoginAuthenticationToken authenticationResult) {
		return new OAuth2AuthenticationToken(authenticationResult.getPrincipal(), authenticationResult.getAuthorities(),
			authenticationResult.getClientRegistration().getRegistrationId());
	}
}
