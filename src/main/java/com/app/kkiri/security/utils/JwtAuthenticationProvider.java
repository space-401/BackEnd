package com.app.kkiri.security.utils;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.app.kkiri.security.model.AuthenticatedUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class JwtAuthenticationProvider implements AuthenticationProvider {
// 	private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationProvider.class);
//
// 	@Override
// 	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
// 		return null;
// 	}
//
// 	@Override
// 	public boolean supports(Class<?> authentication) {
// 		return (AuthenticatedUser.class.isAssignableFrom(authentication))
// 	}
// }
























