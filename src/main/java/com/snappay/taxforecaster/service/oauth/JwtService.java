package com.snappay.taxforecaster.service.oauth;


import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.service.user.UserService;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;

@Service
public class JwtService {

    private final long jwtExpirationSecond;
    private final String secretKey;
    private final UserService userService;

    public JwtService(@Value("jwt.oauth.expiration") long jwtExpirationSecond, @Value("jwt.secret.key") String secretKey, UserService userService) {
        this.jwtExpirationSecond = jwtExpirationSecond;
        this.secretKey = secretKey;
        this.userService = userService;
    }

    private Key getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
    }

    public OAuth2AccessToken generateToken(String username) {
        Instant expireAt = LocalDateTime.now().plusSeconds(jwtExpirationSecond).toInstant(ZoneOffset.UTC);
        Instant issueAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        String token = Jwts.builder()
                .expiration(Date.from(expireAt))
                .claim("iat", Date.from(issueAt))
                .claim("username", username)
                .signWith(this.getSigningKey())
                .compact();
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, token, issueAt, expireAt);
    }

    public boolean validateToken(String token, String username) {
        String usernameToken = this.getUsername(token);
        boolean expired = this.checkExpiration(token);
        return username.equals(usernameToken) && !expired;
    }

    private boolean checkExpiration(String token) {
        return Jwts.parser().verifyWith((SecretKey) this.getSigningKey()).build()
                .parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    private String getUsername(String token) {
        return Jwts.parser().verifyWith((SecretKey) this.getSigningKey()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public OAuth2AccessToken getAccessToken(TaxUser user) {
        boolean exist = userService.checkExistUser(user);
        if (!exist) {
            throw new NotAcceptableException(Collections.singletonList("user.not.found"));
        }
        return this.generateToken(user.getUsername());
    }
}
