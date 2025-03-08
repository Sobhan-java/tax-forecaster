package com.snappay.taxforecaster.service.oauth;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.controller.model.TokenModel;
import com.snappay.taxforecaster.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
public class JwtService {

    private final Cache<String, TokenModel> cache = Caffeine.newBuilder()
            .maximumSize(100000)
            .build();
    private final Long jwtExpirationSecond;
    private final String secretKey;
    private final String tokenScopes;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    public JwtService(@Value("${jwt.oauth.expiration}") Long jwtExpirationSecond, @Value("${jwt.secret.key}") String secretKey, @Value("${jwt.token.scope}") String tokenScopes) {
        this.jwtExpirationSecond = jwtExpirationSecond;
        this.secretKey = secretKey;
        this.tokenScopes = tokenScopes;
    }

    public boolean validateToken(String token) {
        TokenModel tokenModel = cache.getIfPresent(token);
        if (cache.estimatedSize() == 0 || null == tokenModel) {
            return false;
        }
        if (LocalDateTime.now().toInstant(ZoneOffset.UTC).isAfter(tokenModel.getExpiresAt())) {
            cache.invalidate(tokenModel.getTokenValue());
            return false;
        }
        return true;
    }

    public TokenModel getAccessToken(UserEntity entity) {
        Key signingKey = this.getSigningKey(signatureAlgorithm);
        Instant expireAt = LocalDateTime.now().plusSeconds(jwtExpirationSecond).toInstant(ZoneOffset.UTC);
        Instant issueAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(issueAt))
                .setExpiration(Date.from(expireAt))
                .setSubject(entity.getUsername())
                .signWith(signatureAlgorithm, signingKey);

        String[] splitScope = tokenScopes.split(",");
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, builder.compact(), issueAt, expireAt, Set.of(splitScope));

        TokenModel tokenModel = this.convert(oAuth2AccessToken);
        cache.put(oAuth2AccessToken.getTokenValue(), tokenModel);
        return tokenModel;
    }

    private TokenModel convert(OAuth2AccessToken oAuth2AccessToken) {
        TokenModel tokenModel = new TokenModel();
        tokenModel.setTokenValue(oAuth2AccessToken.getTokenValue());
        tokenModel.setIssuedAt(oAuth2AccessToken.getIssuedAt());
        tokenModel.setExpiresAt(oAuth2AccessToken.getExpiresAt());
        tokenModel.setScopes(oAuth2AccessToken.getScopes());
        tokenModel.setTokenType(oAuth2AccessToken.getTokenType().getValue());
        return tokenModel;
    }

    private Key getSigningKey(SignatureAlgorithm signatureAlgorithm) {
        byte[] apiKeySecretBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }

    public TaxUser extractToken(String token) {
        Key signingKey = this.getSigningKey(signatureAlgorithm);
        Claims claims = Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
        TaxUser taxUser = new TaxUser(claims.getSubject());
        taxUser.setId(claims.getId());
        return taxUser;
    }
}
