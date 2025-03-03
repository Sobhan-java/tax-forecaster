package com.snappay.taxforecaster.service.oauth;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.snappay.taxforecaster.entity.UserEntity;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class JwtService {

    private final Map<String, String> tokens = new HashMap<>();
    private final Long jwtExpirationSecond;
    private final String secretKey;
    private final String tokenScopes;

    public JwtService(@Value("${jwt.oauth.expiration}") Long jwtExpirationSecond, @Value("${jwt.secret.key}") String secretKey, @Value("${jwt.token.scope}") String tokenScopes) {
        this.jwtExpirationSecond = jwtExpirationSecond;
        this.secretKey = secretKey;
        this.tokenScopes = tokenScopes;
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
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] apiKeySecretBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        Instant expireAt = LocalDateTime.now().plusSeconds(jwtExpirationSecond).toInstant(ZoneOffset.UTC);
        Instant issueAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(issueAt))
                .setExpiration(Date.from(expireAt))
                .setSubject(entity.getUsername())
                .signWith(signatureAlgorithm, signingKey);

        String[] splitScope = tokenScopes.split(",");
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, builder.compact(), issueAt, expireAt, Set.of(splitScope));
    }
}
