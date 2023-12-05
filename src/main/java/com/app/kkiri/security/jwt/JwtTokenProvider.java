package com.app.kkiri.security.jwt;

import static com.app.kkiri.global.exception.ExceptionCode.*;

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

import com.app.kkiri.global.exception.AuthException;

import io.jsonwebtoken.ExpiredJwtException;
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

		String createAccessToken = Jwts.builder()
			.subject(Long.toString(nextUserId))
			.issuedAt(now)
			.expiration(new Date(now.getTime() + accessTokenValidMilliSecond))
			.signWith(secretKey)
			.compact();
		LOGGER.info("[createAccessToken()] createAccessToken : {}", createAccessToken);

		return createAccessToken;
	}

	public String createRefreshToken(Long nextUserId) {
		Date now = new Date();

		String createRefreshToken = Jwts.builder()
			.subject(Long.toString(nextUserId))
			.issuedAt(now)
			.expiration(new Date(now.getTime() + refreshTokenValidMilliSecond))
			.signWith(secretKey)
			.compact();
		LOGGER.info("[createRefreshToken()] createRefreshToken : {}", createRefreshToken);

		return createRefreshToken;
	}

	// 토큰으로 회원 고유 번호 추출
	public Long getUserIdByToken(String token) {

		try {
			String sub = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
			LOGGER.info("[getUserId()] sub : {}", sub);

			return Long.valueOf(sub);
		} catch(JwtException ex) {
			throw new AuthException(INVALID_TOKEN);
		}
	}

	// 헤더에서 회원 고유 번호 추출
	public Long getUserIdByHttpRequest(HttpServletRequest httpServletRequest) {

		String token = resolveToken(httpServletRequest);

		try{
			String sub = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
			LOGGER.info("[getUserId()] sub : {}", sub);

			return Long.valueOf(sub);
		} catch (ExpiredJwtException ex) {
			throw new AuthException(EXPIRED_TOKEN);
		} catch (JwtException ex) {
			throw new AuthException(INVALID_TOKEN);
		}
	}

	// 토큰의 유효성과 만료일 체크
	public boolean validateToken(String token) {

		try {
			Date expirationDate = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
			Date now = new Date();

			boolean result = expirationDate.after(now);
			LOGGER.info("[validateToken()] result : {}", result);

			return result;
		} catch (ExpiredJwtException ex) {
			throw new AuthException(EXPIRED_TOKEN);
		} catch (JwtException ex){
			throw new AuthException(INVALID_TOKEN);
		}
	}

	// 헤더에서 토큰 추출
	public String resolveToken(HttpServletRequest request) {

		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header == null) {
			throw new AuthException(TOKEN_NOT_FOUND);
		}

		header = header.trim();
		if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BEARER)) {
			throw new AuthException(TOKEN_NOT_FOUND);
		}

		if (header.equalsIgnoreCase(AUTHENTICATION_SCHEME_BEARER)) {
			throw new AuthException(TOKEN_NOT_FOUND);
		}

		String resolvedToken = header.substring(7);
		LOGGER.info("[resolveToken()] resolvedToken : {}", resolvedToken);

		return resolvedToken;
	}

	// 토큰으로 인증 정보 조회
	public Authentication getAuthentication(String token) throws AuthenticationException {

		Long userId = this.getUserIdByToken(token);
		if(userId == null) {
			throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT), "존재하지 않는 사용자입니다");
		}

		String userIdStr = String.valueOf(this.getUserIdByToken(token));
		UserDetails userDetails = userDetailsService.loadUserByUsername(userIdStr);

		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	// 리프레시 토큰을 사용하여 액세스 토큰을 재발급한다.
	public String reissueAccessToken(final HttpServletRequest httpServletRequest) {

		final String refreshToken = resolveToken(httpServletRequest);

		if(validateToken(refreshToken)) {
			final Long userId = getUserIdByToken(refreshToken);
			final String reissuedAccessToken = createAccessToken(userId);

			return reissuedAccessToken;
		}

		throw new AuthException(FAIL_TO_VALIDATE_TOKEN);
	}
}