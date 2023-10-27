package com.app.kkiri.security.utils;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

	/**
	 * HTTP Request Header 에 설정된 토큰 값을 가져옴
	 *
	 * @param request - 인증 요청
	 * @return String 타입의 토큰
	 */
	public String resolveToken(HttpServletRequest request) {
		String value = request.getHeader("Authorization");

		if(value == null) {
			return null;
		}

		int endIndex = value.indexOf("Bearer") + 6; // "Bearer 토큰값" 일 경우 6을 리턴한다.

		return value.substring(endIndex + 1); //"Bearer 토큰값" 일 경우 "토큰값"만 리턴한다.
	}
}
























