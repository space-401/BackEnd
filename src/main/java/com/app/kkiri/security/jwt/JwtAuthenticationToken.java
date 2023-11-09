package com.app.kkiri.security.jwt;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import lombok.ToString;

@ToString
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

	private String jwt;
	private JwtAuthenticatedPrincipal jwtAuthenticatedPrincipal;

	public JwtAuthenticationToken(String jwt) {
		super(Collections.emptyList());
		this.jwt = jwt;
	}

	public JwtAuthenticationToken(JwtAuthenticatedPrincipal jwtAuthenticatedPrincipal) {
		super(Collections.emptyList());
		this.jwtAuthenticatedPrincipal = jwtAuthenticatedPrincipal;
	}

	@Override
	public Object getCredentials() {
		return "";
	}

	@Override
	public Object getPrincipal() {
		return this.jwtAuthenticatedPrincipal;
	}

	public String getJwt() {
		return jwt;
	}
}
