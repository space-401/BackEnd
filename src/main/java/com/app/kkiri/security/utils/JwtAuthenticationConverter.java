package com.app.kkiri.security.utils;

/*@RequiredArgsConstructor
public class JwtAuthenticationConverter implements AuthenticationConverter {

	public static final String AUTHENTICATION_SCHEME_BEARER = "bearer";

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public Authentication convert(HttpServletRequest request) {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (header == null) {
			return null;
		}

		header = header.trim();

		if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BEARER)) {
			return null;
		}

		if (header.equalsIgnoreCase(AUTHENTICATION_SCHEME_BEARER)) {
			throw new BadCredentialsException("Empty basic authentication token");
		}

		String token = header.substring(7);
		Long userId = jwtTokenProvider.getUserId(token);

		AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
			.userResponseDTO(
				UserResponseDTO.builder()
					.userId(userId)
					.build()
			)
			.build();

		return authenticatedUser;
	}
}*/
