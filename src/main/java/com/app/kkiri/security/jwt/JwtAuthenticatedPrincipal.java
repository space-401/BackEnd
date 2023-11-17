package com.app.kkiri.security.jwt;

import java.util.Map;

import org.springframework.security.core.AuthenticatedPrincipal;

public interface JwtAuthenticatedPrincipal extends AuthenticatedPrincipal {

	Map<String, Object> getAttributes();
}
