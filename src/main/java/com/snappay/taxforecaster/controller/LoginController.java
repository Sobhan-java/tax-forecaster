package com.snappay.taxforecaster.controller;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.service.oauth.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/oauth")
public class LoginController {

    private final JwtService jwtService;

    public LoginController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Operation(summary = "وظیفه لاگین کردن کاربر",description = "زمانیکه نام کاربری و یا رمز عبور کاربر اشتباه یک ارور کاربر یافت نشد میدهد")
    @PostMapping("/login")
    public OAuth2AccessToken login(@RequestBody TaxUser user){
        return jwtService.getAccessToken(user);
    }
}
