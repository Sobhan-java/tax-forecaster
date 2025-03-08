package com.snappay.taxforecaster.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenModel {

    private String tokenValue;
    private Instant issuedAt;
    private Instant expiresAt;
    private Set<String> scopes;
    private String tokenType;
}
