package com.snappay.taxforecaster.controller;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.model.TaxPrediction;
import com.snappay.taxforecaster.service.taxprediction.TaxPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/tax-prediction")
public class TaxPredictionController {

    private final TaxPredictionService service;

    public TaxPredictionController(TaxPredictionService service) {
        this.service = service;
    }

    @Operation(summary = "محاسبه مقدار مالیات", description = "محاسبه مقدار مالیات براساس مقدار حقوق کاربر لاگین شده")
    @GetMapping("/calculate")
    public ResponseEntity<TaxPrediction> calculate(@RequestParam("salary") BigDecimal salary) {
        TaxUser user = (TaxUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.calculateTaxPrediction(salary, user));
    }
}