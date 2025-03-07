package com.snappay.taxforecaster.config;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.entity.UserEntity;
import com.snappay.taxforecaster.service.oauth.JwtService;
import com.snappay.taxforecaster.service.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class OauthTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public OauthTokenFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(tokenHeader) || !tokenHeader.startsWith("Bearer")) {
            doFilter(request, response, filterChain);
            return;
        }
        String token = tokenHeader.replace("Bearer ", "");
        if (!jwtService.validateToken(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "token.not.valid");
            doFilter(request, response, filterChain);
            return;
        }
        TaxUser user = this.getUser(token);
        if (null == user) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "token.not.valid");
        }
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user, null, null);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("token set in security context holder");
        doFilter(request, response, filterChain);
    }

    private TaxUser getUser(String token) {
        TaxUser user = jwtService.extractToken(token);
        UserEntity entity = userService.getOne(user.getUsername());
        if (null == entity) {
            return null;
        }
        user = new TaxUser(entity.getUsername());
        user.setId(entity.getId());
        return user;
    }
}
