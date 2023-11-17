package com.app.kkiri.security.jwt;

import java.util.Map;

import lombok.ToString;

@ToString
public class DefaultJwtUser implements JwtAuthenticatedPrincipal {

	private final Map<String, Object> attributes;

	public DefaultJwtUser(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getName() {
		return (String)this.attributes.get("userId");
	}
}
