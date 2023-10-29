package com.app.kkiri.security.utils;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
	private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${jwt.secret}")
	private String secret;

	private SecretKey secretKey;

	private final Long accessTokenValidMilliSecond = 1000L * 60 * 30; // 30분

	private final Long refreshTokenValidMilliSecond = 1000L * 60 * 60 * 24 * 14; // 2주

	@PostConstruct
	protected void init() {
		secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
	}

	public String createAccessToken(Long nextUserId) {
		Date now = new Date();

		return Jwts.builder()
			.subject(Long.toString(nextUserId))
			.issuedAt(now)
			.expiration(new Date(now.getTime() + accessTokenValidMilliSecond))
			.signWith(secretKey)
			.compact();
	}

	public String createRefreshToken(Long nextUserId) {
		Date now = new Date();

		return Jwts.builder()
			.subject(Long.toString(nextUserId))
			.issuedAt(now)
			.expiration(new Date(now.getTime() + refreshTokenValidMilliSecond))
			.signWith(secretKey)
			.compact();
	}

	// 엑세스 토큰으로 회원 고유 번호 추출
	public Long getUserId(String token) {
		Jws<Claims> jws;

		try {
			jws = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);

			return Long.valueOf(jws.getPayload().getSubject());

		} catch (JwtException ex){
			return null;
		}
	}

	// 토큰의 유효성과 만료일 체크
	public boolean validateAccessToken(String token) {
		try {
			Jws<Claims> jws = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token);

			return !jws.getPayload().getExpiration().before(new Date());

		} catch (JwtException ex){
			return false;
		}
	}
}
























