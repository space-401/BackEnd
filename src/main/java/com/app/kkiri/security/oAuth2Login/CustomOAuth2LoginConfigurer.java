package com.app.kkiri.security.oAuth2Login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationProvider;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.CustomUserTypesOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DelegatingOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public final class CustomOAuth2LoginConfigurer
	extends AbstractAuthenticationFilterConfigurer<HttpSecurity, CustomOAuth2LoginConfigurer, CustomOAuth2LoginAuthenticationFilter>{

	private final AuthorizationEndpointConfig authorizationEndpointConfig = new AuthorizationEndpointConfig();
	private final TokenEndpointConfig tokenEndpointConfig = new TokenEndpointConfig();
	private final RedirectionEndpointConfig redirectionEndpointConfig = new RedirectionEndpointConfig();
	private final UserInfoEndpointConfig userInfoEndpointConfig = new UserInfoEndpointConfig();
	private String loginPage;
	private String loginProcessingUrl = CustomOAuth2LoginAuthenticationFilter.DEFAULT_FILTER_PROCESSES_URI;

	// 191
	@Override
	public CustomOAuth2LoginConfigurer loginPage(String loginPage) {
		Assert.hasText(loginPage, "loginPage cannot be empty");
		this.loginPage = loginPage;
		return this;
	}

	// 198
	@Override
	public CustomOAuth2LoginConfigurer loginProcessingUrl(String loginProcessingUrl) {
		Assert.hasText(loginProcessingUrl, "loginProcessingUrl cannot be empty");
		this.loginProcessingUrl = loginProcessingUrl;
		return this;
	}

	// 210
	public AuthorizationEndpointConfig authorizationEndpoint() {
		return this.authorizationEndpointConfig;
	}

	// 220
	public CustomOAuth2LoginConfigurer authorizationEndpoint(
		Customizer<AuthorizationEndpointConfig> authorizationEndpointCustomizer) {
		authorizationEndpointCustomizer.customize(this.authorizationEndpointConfig);
		return this;
	}

	// 231
	public TokenEndpointConfig tokenEndpoint() {
		return this.tokenEndpointConfig;
	}

	// 242
	public CustomOAuth2LoginConfigurer tokenEndpoint(Customizer<TokenEndpointConfig> tokenEndpointCustomizer) {
		tokenEndpointCustomizer.customize(this.tokenEndpointConfig);
		return this;
	}

	// 252
	public RedirectionEndpointConfig redirectionEndpoint() {
		return this.redirectionEndpointConfig;
	}

	// 262
	public CustomOAuth2LoginConfigurer redirectionEndpoint(
		Customizer<RedirectionEndpointConfig> redirectionEndpointCustomizer) {
		redirectionEndpointCustomizer.customize(this.redirectionEndpointConfig);
		return this;
	}

	// 273
	public UserInfoEndpointConfig userInfoEndpoint() {
		return this.userInfoEndpointConfig;
	}

	// 283
	public CustomOAuth2LoginConfigurer userInfoEndpoint(Customizer<UserInfoEndpointConfig> userInfoEndpointCustomizer) {
		userInfoEndpointCustomizer.customize(this.userInfoEndpointConfig);
		return this;
	}

	@Override
	public void init(HttpSecurity http) throws Exception {
		CustomOAuth2LoginAuthenticationFilter customOAuth2LoginAuthenticationFilter = new CustomOAuth2LoginAuthenticationFilter(
			CustomOAuth2ClientConfigurerUtils.getClientRegistrationRepository(this.getBuilder()),
			CustomOAuth2ClientConfigurerUtils.getAuthorizedClientRepository(this.getBuilder()),
			this.loginProcessingUrl);
		this.setAuthenticationFilter(customOAuth2LoginAuthenticationFilter);
		super.loginProcessingUrl(this.loginProcessingUrl);
		if (this.loginPage != null) {
			// Set custom login page
			super.loginPage(this.loginPage);
			super.init(http);
		}
		else {
			Map<String, String> loginUrlToClientName = this.getLoginLinks();
			if (loginUrlToClientName.size() == 1) {
				// Setup auto-redirect to provider login page
				// when only 1 client is configured
				this.updateAuthenticationDefaults();
				this.updateAccessDefaults(http);
				String providerLoginPage = loginUrlToClientName.keySet().iterator().next();
				this.registerAuthenticationEntryPoint(http, this.getLoginEntryPoint(http, providerLoginPage));
			}
			else {
				super.init(http);
			}
		}
		OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient = this.tokenEndpointConfig.accessTokenResponseClient;
		if (accessTokenResponseClient == null) {
			accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
		}
		OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService = getOAuth2UserService();
		OAuth2LoginAuthenticationProvider oauth2LoginAuthenticationProvider = new OAuth2LoginAuthenticationProvider(
			accessTokenResponseClient, oauth2UserService);
		GrantedAuthoritiesMapper userAuthoritiesMapper = this.getGrantedAuthoritiesMapper();
		if (userAuthoritiesMapper != null) {
			oauth2LoginAuthenticationProvider.setAuthoritiesMapper(userAuthoritiesMapper);
		}
		http.authenticationProvider(this.postProcess(oauth2LoginAuthenticationProvider));
		boolean oidcAuthenticationProviderEnabled = ClassUtils
			.isPresent("org.springframework.security.oauth2.jwt.JwtDecoder", this.getClass().getClassLoader());
		if (oidcAuthenticationProviderEnabled) {
			OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService = getOidcUserService();
			OidcAuthorizationCodeAuthenticationProvider oidcAuthorizationCodeAuthenticationProvider = new OidcAuthorizationCodeAuthenticationProvider(
				accessTokenResponseClient, oidcUserService);
			JwtDecoderFactory<ClientRegistration> jwtDecoderFactory = this.getJwtDecoderFactoryBean();
			if (jwtDecoderFactory != null) {
				oidcAuthorizationCodeAuthenticationProvider.setJwtDecoderFactory(jwtDecoderFactory);
			}
			if (userAuthoritiesMapper != null) {
				oidcAuthorizationCodeAuthenticationProvider.setAuthoritiesMapper(userAuthoritiesMapper);
			}
			http.authenticationProvider(this.postProcess(oidcAuthorizationCodeAuthenticationProvider));
		}
		else {
			http.authenticationProvider(new OidcAuthenticationRequestChecker());
		}
		this.initDefaultLoginFilter(http);

		//
		http.addFilterAfter(customOAuth2LoginAuthenticationFilter, LogoutFilter.class);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		CustomOAuth2LoginAuthenticationFilter authenticationFilter = this.getAuthenticationFilter();
		if (this.redirectionEndpointConfig.authorizationResponseBaseUri != null) {
			authenticationFilter.setFilterProcessesUrl(this.redirectionEndpointConfig.authorizationResponseBaseUri);
		}
		if (this.authorizationEndpointConfig.authorizationRequestRepository != null) {
			authenticationFilter
				.setAuthorizationRequestRepository(this.authorizationEndpointConfig.authorizationRequestRepository);
		}
		super.configure(http);
	}

	// 384
	@Override
	protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
		return new AntPathRequestMatcher(loginProcessingUrl);
	}

	// 388
	@SuppressWarnings("unchecked")
	private JwtDecoderFactory<ClientRegistration> getJwtDecoderFactoryBean() {
		ResolvableType type = ResolvableType.forClassWithGenerics(JwtDecoderFactory.class, ClientRegistration.class);
		String[] names = this.getBuilder().getSharedObject(ApplicationContext.class).getBeanNamesForType(type);
		if (names.length > 1) {
			throw new NoUniqueBeanDefinitionException(type, names);
		}
		if (names.length == 1) {
			return (JwtDecoderFactory<ClientRegistration>) this.getBuilder().getSharedObject(ApplicationContext.class)
				.getBean(names[0]);
		}
		return null;
	}

	// 402
	private GrantedAuthoritiesMapper getGrantedAuthoritiesMapper() {
		GrantedAuthoritiesMapper grantedAuthoritiesMapper = this.getBuilder()
			.getSharedObject(GrantedAuthoritiesMapper.class);
		if (grantedAuthoritiesMapper == null) {
			grantedAuthoritiesMapper = this.getGrantedAuthoritiesMapperBean();
			if (grantedAuthoritiesMapper != null) {
				this.getBuilder().setSharedObject(GrantedAuthoritiesMapper.class, grantedAuthoritiesMapper);
			}
		}
		return grantedAuthoritiesMapper;
	}

	// 414
	private GrantedAuthoritiesMapper getGrantedAuthoritiesMapperBean() {
		Map<String, GrantedAuthoritiesMapper> grantedAuthoritiesMapperMap = BeanFactoryUtils
			.beansOfTypeIncludingAncestors(this.getBuilder().getSharedObject(ApplicationContext.class),
				GrantedAuthoritiesMapper.class);
		return (!grantedAuthoritiesMapperMap.isEmpty() ? grantedAuthoritiesMapperMap.values().iterator().next() : null);
	}

	// 421
	private OAuth2UserService<OidcUserRequest, OidcUser> getOidcUserService() {
		if (this.userInfoEndpointConfig.oidcUserService != null) {
			return this.userInfoEndpointConfig.oidcUserService;
		}
		ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2UserService.class, OidcUserRequest.class,
			OidcUser.class);
		OAuth2UserService<OidcUserRequest, OidcUser> bean = getBeanOrNull(type);
		return (bean != null) ? bean : new OidcUserService();
	}

	// 431
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> getOAuth2UserService() {
		if (this.userInfoEndpointConfig.userService != null) {
			return this.userInfoEndpointConfig.userService;
		}
		ResolvableType type = ResolvableType.forClassWithGenerics(OAuth2UserService.class, OAuth2UserRequest.class,
			OAuth2User.class);
		OAuth2UserService<OAuth2UserRequest, OAuth2User> bean = getBeanOrNull(type);
		if (bean != null) {
			return bean;
		}
		if (this.userInfoEndpointConfig.customUserTypes.isEmpty()) {
			return new DefaultOAuth2UserService();
		}
		List<OAuth2UserService<OAuth2UserRequest, OAuth2User>> userServices = new ArrayList<>();
		userServices.add(new CustomUserTypesOAuth2UserService(this.userInfoEndpointConfig.customUserTypes));
		userServices.add(new DefaultOAuth2UserService());
		return new DelegatingOAuth2UserService<>(userServices);
	}

	// 450
	private <T> T getBeanOrNull(ResolvableType type) {
		ApplicationContext context = getBuilder().getSharedObject(ApplicationContext.class);
		if (context != null) {
			String[] names = context.getBeanNamesForType(type);
			if (names.length == 1) {
				return (T) context.getBean(names[0]);
			}
		}
		return null;
	}

	// 461
	private void initDefaultLoginFilter(HttpSecurity http) {
		DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http
			.getSharedObject(DefaultLoginPageGeneratingFilter.class);
		if (loginPageGeneratingFilter == null || this.isCustomLoginPage()) {
			return;
		}
		loginPageGeneratingFilter.setOauth2LoginEnabled(true);
		loginPageGeneratingFilter.setOauth2AuthenticationUrlToClientName(this.getLoginLinks());
		loginPageGeneratingFilter.setLoginPageUrl(this.getLoginPage());
		loginPageGeneratingFilter.setFailureUrl(this.getFailureUrl());
	}

	// 474
	private Map<String, String> getLoginLinks() {
		Iterable<ClientRegistration> clientRegistrations = null;
		ClientRegistrationRepository clientRegistrationRepository = CustomOAuth2ClientConfigurerUtils
			.getClientRegistrationRepository(this.getBuilder());
		ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
		if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
			clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
		}
		if (clientRegistrations == null) {
			return Collections.emptyMap();
		}
		String authorizationRequestBaseUri = (this.authorizationEndpointConfig.authorizationRequestBaseUri != null)
			? this.authorizationEndpointConfig.authorizationRequestBaseUri
			: OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
		Map<String, String> loginUrlToClientName = new HashMap<>();
		clientRegistrations.forEach((registration) -> {
			if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(registration.getAuthorizationGrantType())) {
				String authorizationRequestUri = authorizationRequestBaseUri + "/" + registration.getRegistrationId();
				loginUrlToClientName.put(authorizationRequestUri, registration.getClientName());
			}
		});
		return loginUrlToClientName;
	}

	// 498
	private AuthenticationEntryPoint getLoginEntryPoint(HttpSecurity http, String providerLoginPage) {
		RequestMatcher loginPageMatcher = new AntPathRequestMatcher(this.getLoginPage());
		RequestMatcher faviconMatcher = new AntPathRequestMatcher("/favicon.ico");
		RequestMatcher defaultEntryPointMatcher = this.getAuthenticationEntryPointMatcher(http);
		RequestMatcher defaultLoginPageMatcher = new AndRequestMatcher(
			new OrRequestMatcher(loginPageMatcher, faviconMatcher), defaultEntryPointMatcher);
		RequestMatcher notXRequestedWith = new NegatedRequestMatcher(
			new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
		entryPoints.put(new AndRequestMatcher(notXRequestedWith, new NegatedRequestMatcher(defaultLoginPageMatcher)),
			new LoginUrlAuthenticationEntryPoint(providerLoginPage));
		DelegatingAuthenticationEntryPoint loginEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
		loginEntryPoint.setDefaultEntryPoint(this.getAuthenticationEntryPoint());
		return loginEntryPoint;
	}

	// 517
	public final class AuthorizationEndpointConfig {

		private String authorizationRequestBaseUri;

		private OAuth2AuthorizationRequestResolver authorizationRequestResolver;

		private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

		private AuthorizationEndpointConfig() {
		}

		public AuthorizationEndpointConfig baseUri(String authorizationRequestBaseUri) {
			Assert.hasText(authorizationRequestBaseUri, "authorizationRequestBaseUri cannot be empty");
			this.authorizationRequestBaseUri = authorizationRequestBaseUri;
			return this;
		}

		public AuthorizationEndpointConfig authorizationRequestResolver(
			OAuth2AuthorizationRequestResolver authorizationRequestResolver) {
			Assert.notNull(authorizationRequestResolver, "authorizationRequestResolver cannot be null");
			this.authorizationRequestResolver = authorizationRequestResolver;
			return this;
		}

		public AuthorizationEndpointConfig authorizationRequestRepository(
			AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository) {
			Assert.notNull(authorizationRequestRepository, "authorizationRequestRepository cannot be null");
			this.authorizationRequestRepository = authorizationRequestRepository;
			return this;
		}

		public CustomOAuth2LoginConfigurer and() {
			return CustomOAuth2LoginConfigurer.this;
		}

	}

	// 580
	public final class TokenEndpointConfig {

		private OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

		private TokenEndpointConfig() {
		}

		public TokenEndpointConfig accessTokenResponseClient(
			OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient) {
			Assert.notNull(accessTokenResponseClient, "accessTokenResponseClient cannot be null");
			this.accessTokenResponseClient = accessTokenResponseClient;
			return this;
		}

		public CustomOAuth2LoginConfigurer and() {
			return CustomOAuth2LoginConfigurer.this;
		}

	}

	// 614
	public final class RedirectionEndpointConfig {

		private String authorizationResponseBaseUri;

		private RedirectionEndpointConfig() {
		}

		public RedirectionEndpointConfig baseUri(String authorizationResponseBaseUri) {
			Assert.hasText(authorizationResponseBaseUri, "authorizationResponseBaseUri cannot be empty");
			this.authorizationResponseBaseUri = authorizationResponseBaseUri;
			return this;
		}

		public CustomOAuth2LoginConfigurer and() {
			return CustomOAuth2LoginConfigurer.this;
		}

	}

	// 646
	public final class UserInfoEndpointConfig {

		private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;

		private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;

		private Map<String, Class<? extends OAuth2User>> customUserTypes = new HashMap<>();

		private UserInfoEndpointConfig() {
		}

		public UserInfoEndpointConfig userService(OAuth2UserService<OAuth2UserRequest, OAuth2User> userService) {
			Assert.notNull(userService, "userService cannot be null");
			this.userService = userService;
			return this;
		}

		public UserInfoEndpointConfig oidcUserService(OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService) {
			Assert.notNull(oidcUserService, "oidcUserService cannot be null");
			this.oidcUserService = oidcUserService;
			return this;
		}

		@Deprecated
		public UserInfoEndpointConfig customUserType(Class<? extends OAuth2User> customUserType,
			String clientRegistrationId) {
			Assert.notNull(customUserType, "customUserType cannot be null");
			Assert.hasText(clientRegistrationId, "clientRegistrationId cannot be empty");
			this.customUserTypes.put(clientRegistrationId, customUserType);
			return this;
		}

		public UserInfoEndpointConfig userAuthoritiesMapper(GrantedAuthoritiesMapper userAuthoritiesMapper) {
			Assert.notNull(userAuthoritiesMapper, "userAuthoritiesMapper cannot be null");
			CustomOAuth2LoginConfigurer.this.getBuilder().setSharedObject(GrantedAuthoritiesMapper.class,
				userAuthoritiesMapper);
			return this;
		}

		public CustomOAuth2LoginConfigurer and() {
			return CustomOAuth2LoginConfigurer.this;
		}

	}

	// 724
	private static class OidcAuthenticationRequestChecker implements AuthenticationProvider {

		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {
			OAuth2LoginAuthenticationToken authorizationCodeAuthentication = (OAuth2LoginAuthenticationToken) authentication;
			OAuth2AuthorizationRequest authorizationRequest = authorizationCodeAuthentication.getAuthorizationExchange()
				.getAuthorizationRequest();
			if (authorizationRequest.getScopes().contains(OidcScopes.OPENID)) {
				// Section 3.1.2.1 Authentication Request -
				// https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest scope
				// REQUIRED. OpenID Connect requests MUST contain the "openid" scope
				// value.
				OAuth2Error oauth2Error = new OAuth2Error("oidc_provider_not_configured",
					"An OpenID Connect Authentication Provider has not been configured. "
						+ "Check to ensure you include the dependency 'spring-security-oauth2-jose'.",
					null);
				throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
			}
			return null;
		}

		@Override
		public boolean supports(Class<?> authentication) {
			return OAuth2LoginAuthenticationToken.class.isAssignableFrom(authentication);
		}

	}
}
