package com.snappay.taxforecaster;


import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AppController {

    @Operation(summary = "چک کردن بالا بودن پروژه")
    @GetMapping("/health")
    public String healthCheck(){
        return "UP";
    }
}
