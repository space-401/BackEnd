package com.app.kkiri.security.filters;

// @RequiredArgsConstructor
// public class JwtAuthenticationFilter extends OncePerRequestFilter {
//     private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
//
//     private final JwtAuthenticationConverter jwtAuthenticationConverter;
//
//     private final JwtAuthenticationProvider jwtAuthenticationProvider;
//
//     @Override
//     protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse
//         ,FilterChain filterChain) throws ServletException, IOException {
//         LOGGER.info("params servletRequest : {}, servletResponse : {}, filterChain : {}", servletRequest, servletResponse, filterChain);
//
//         jwtAuthenticationProvider.authenticate(au)
//         LOGGER.info("[doFilterInternal()] returned value token : {}", token);
//
//
//         filterChain.doFilter(servletRequest, servletResponse);
//     }
// }