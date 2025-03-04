package com.snappay.taxforecaster.config;

import com.snappay.taxforecaster.service.oauth.JwtService;
import com.snappay.taxforecaster.service.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final JwtService jwtService;
    private final UserService userService;

    public SecurityConfiguration(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(auth -> auth.requestMatchers("/app/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/**",
                        "/swagger-resources/**",
                        "/webjars/**").permitAll().anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(configure -> configure.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterAfter(new OauthTokenFilter(jwtService, userService), BasicAuthenticationFilter.class)
                .build();
    }
}
