package com.snappay.taxforecaster.controller;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.controller.model.TaxPrediction;
import com.snappay.taxforecaster.service.taxprediction.TaxPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        BigDecimal taxAmount = service.calculateTax(salary, user);
        return ResponseEntity.ok(new TaxPrediction(taxAmount, salary));
    }

    @Operation(summary = "محاسبه مقدار مالیات براساس تاریخ ورودی", description = "محاسبه مقدار مالیات براساس بازه زمانی وارد شده که در صورت وارد نکردن بازه به صورت پیش فرض سالیانه در نظر گرفته میشود")
    @GetMapping("/total-salary")
    public ResponseEntity<TaxPrediction> calculate(@RequestParam(value = "startDate", required = false) LocalDateTime startDate,
                                                   @RequestParam(value = "endDate", required = false) LocalDateTime endDate) {
        TaxUser user = (TaxUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.getTotalTaxAmount(startDate, endDate, user));
    }
}