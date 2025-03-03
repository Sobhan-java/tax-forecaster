package com.snappay.taxforecaster.config;

import com.snappay.taxforecaster.service.oauth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class OauthTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public OauthTokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(tokenHeader) || !tokenHeader.startsWith("Bearer")) {
            doFilter(request, response, filterChain);
            return;
        }
        String token = tokenHeader.replace("Bearer ", "");
        if (jwtService.validateToken(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "token.not.valid");
        }
        doFilter(request, response, filterChain);
    }
}
