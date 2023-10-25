package com.app.kkiri.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.kkiri.common.CustomOAuth2AuthorizationRequestResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {
	private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	private final Log logger = LogFactory.getLog(getClass());

	private final CustomOAuth2AuthorizationRequestResolver resolver;

	private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository =
		new HttpSessionOAuth2AuthorizationRequestRepository();

	@GetMapping("/auth/{socialType}")
	public void oAuth2Login(
		@PathVariable String socialType,
		@RequestParam(required = false) String code,
		@RequestParam(required = false) String state,
		@RequestParam(required = false) String error,
		@RequestParam(value = "error_description", required = false) String errorDescription,
		HttpServletRequest request,
		HttpServletResponse response,
		RedirectAttributes redirectAttributes) throws IOException {
		LOGGER.info("[oAuth2Login()] socialType : {}, code : {}, state : {}, error : {}, errorDescription : {}",
			socialType, code, state, error, errorDescription);

		OAuth2AuthorizationRequest authorizationRequest = this.resolver.resolve(socialType, state);
		LOGGER.info("[resolve()] authorizationRequest : {}", authorizationRequest);

		if (authorizationRequest != null) { // 인가서버로부터 인가 코드 받기 응답 성공한 경우
			redirectAttributes.addAttribute("code", code);
			redirectAttributes.addAttribute("state", state);
		} else { // 인가서버로부터 인가 코드 받기 응답이 실패할 경우
			redirectAttributes.addAttribute("error", error);
			redirectAttributes.addAttribute("error_description ", errorDescription);
		}

		this.saveRedirectForAuthorization(request, response, authorizationRequest);

		String tokenRequestUri = "/login/oauth2/code/" + socialType;

		response.sendRedirect(tokenRequestUri);
	}

	private void saveRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response,
		OAuth2AuthorizationRequest authorizationRequest) {
		if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorizationRequest.getGrantType())) {
			this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
		}
	}

	private void unsuccessfulRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response,
		Exception ex) throws IOException {
		this.logger.error(LogMessage.format("Authorization Request failed: %s", ex), ex);
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
			HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
	}
}