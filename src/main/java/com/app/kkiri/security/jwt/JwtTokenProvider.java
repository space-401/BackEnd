package com.app.kkiri.security.jwt;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

	public static final String AUTHENTICATION_SCHEME_BEARER = "Bearer";

	private final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);
	private final Long accessTokenValidMilliSecond = 1000L * 60 * 30; // 30분
	private final Long refreshTokenValidMilliSecond = 1000L * 60 * 60 * 24 * 14; // 2주
	private final UserDetailsService userDetailsService;

	@Value("${jwt.secret}")
	private String secret;
	private SecretKey secretKey;

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

		try {
			String sub = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
			return Long.valueOf(sub);

		} catch (JwtException ex){
			return null;
		}
	}

	// 토큰의 유효성과 만료일 체크
	public boolean validateToken(String token) {

		try {

			Date date = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
			LOGGER.info("[validateToken()] date.getTime() : {}", date.getTime());

			Date now = new Date();
			LOGGER.info("[validateToken()] now : {}", now);

			LOGGER.info("[validateToken(] result : {}", Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date()));

			return !Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());

		} catch (JwtException ex){
			LOGGER.info("[validateToken()] ex.getMessage() : {}", ex.getMessage());
			LOGGER.info("[validateToken()] ex.getCause() : {}", ex.getCause());
			LOGGER.info("[validateToken()] ex.getClass() : {}", ex.getClass());

			return false;
		}
	}

	// 헤더에서 토큰 추출
	public String resolveToken(HttpServletRequest request) throws AuthenticationException {

		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header == null) {
			return null;
		}

		header = header.trim();
		if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BEARER)) {
			return null;
		}

		if (header.equalsIgnoreCase(AUTHENTICATION_SCHEME_BEARER)) {
			return null;
		}

		return header.substring(7);
	}

	// 토큰으로 인증 정보 조회
	public Authentication getAuthentication(String token) throws AuthenticationException {

		Long userId = this.getUserId(token);
		if(userId == null) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT), "존재하지 않는 사용자입니다");
		}

		String userIdStr = String.valueOf(this.getUserId(token));
		UserDetails userDetails = userDetailsService.loadUserByUsername(userIdStr);

		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

}