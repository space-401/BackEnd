package com.app.kkiri.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.kkiri.security.utils.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
        FilterChain filterChain) throws ServletException, IOException {

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String requestURI = servletRequest.getRequestURI();
        LOGGER.info("doFilterInternal() returned value requestURI : {}", requestURI);

        // 소셜 로그인을 위한 경로를 제외하고 JwtAuthenticationFilter 를 동작시킨다.
        if(!(antPathMatcher.match("/user/auth/**", requestURI) || antPathMatcher.match("/", requestURI))) {
            String accessToken = jwtTokenProvider.resolveToken(servletRequest);
            if(accessToken == null) {
                throw new InsufficientAuthenticationException("올바르지 않은 형식의 토큰입니다");
            }
            LOGGER.info("[doFilterInternal()] returned value accessToken : {}", accessToken);

            if(!jwtTokenProvider.validateToken(accessToken)) {
                throw new InsufficientAuthenticationException("만료된 토큰입니다.");
            }

            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}