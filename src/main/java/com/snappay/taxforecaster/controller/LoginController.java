package com.snappay.taxforecaster.controller;

import com.snappay.taxforecaster.controller.model.TaxUserDto;
import com.snappay.taxforecaster.controller.model.TokenModel;
import com.snappay.taxforecaster.entity.UserEntity;
import com.snappay.taxforecaster.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/oauth")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "وظیفه لاگین کردن کاربر", description = "زمانیکه نام کاربری و یا رمز عبور کاربر اشتباه یک ارور کاربر یافت نشد میدهد")
    @PostMapping("/login")
    public TokenModel login(@RequestBody TaxUserDto user) {
        return userService.login(user);
    }

    @Operation(summary = "ثبت نام کاربر")
    @PostMapping("/register")
    public UserEntity register(@RequestBody TaxUserDto dto) {
        return userService.save(dto);
    }
}
