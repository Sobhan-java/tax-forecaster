package com.snappay.taxforecaster.service.oauth;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.snappay.taxforecaster.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final Map<String, String> tokens = new HashMap<>();
    private final Long jwtExpirationSecond;
    private final String secretKey;

    public JwtService(@Value("${jwt.oauth.expiration}") Long jwtExpirationSecond, @Value("${jwt.secret.key}") String secretKey) {
        this.jwtExpirationSecond = jwtExpirationSecond;
        this.secretKey = secretKey;
    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public OAuth2AccessToken generateToken(String username) {
        Instant expireAt = LocalDateTime.now().plusSeconds(jwtExpirationSecond).toInstant(ZoneOffset.UTC);
        Instant issueAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        String token = Jwts.builder()
                .setExpiration(Date.from(expireAt))
                .setIssuedAt(Date.from(issueAt))
                .setSubject(username)
                .signWith(this.getSigningKey())
                .compact();
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, token, issueAt, expireAt);
    }

    public boolean validateToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        String username = decodedJWT.getSubject();
        if (tokens.get(username).isEmpty()) {
            return false;
        }
        return !this.checkExpiration(decodedJWT);
    }

    private boolean checkExpiration(DecodedJWT decodedJWT) {
        boolean expire = decodedJWT.getExpiresAt().before(new Date());
        if (expire) {
            tokens.remove(decodedJWT.getSubject());
        }
        return expire;
    }

    public OAuth2AccessToken getAccessToken(UserEntity entity) {
        return this.generateToken(entity.getUsername());
    }
}
