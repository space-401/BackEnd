package com.app.kkiri.security.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.kkiri.security.utils.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {
    private final Logger LOGGER = LoggerFactory.getLogger(AccessTokenAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse
        ,FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info("params servletRequest : {}, servletResponse : {}, filterChain : {}", servletRequest, servletResponse, filterChain);

        String token = jwtTokenProvider.resolveToken(servletRequest);
        LOGGER.info("[doFilterInternal()] returned value token : {}", token);

        if (token != null && jwtTokenProvider.validateAccessToken(token)) {

        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}