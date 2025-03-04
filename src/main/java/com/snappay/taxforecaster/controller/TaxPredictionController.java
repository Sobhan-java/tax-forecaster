package com.snappay.taxforecaster.controller;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.entity.TaxPredictionEntity;
import com.snappay.taxforecaster.service.taxprediction.TaxPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tax-prediction")
public class TaxPredictionController {

    private final TaxPredictionService service;

    public TaxPredictionController(TaxPredictionService service) {
        this.service = service;
    }

    @Operation(summary = "محاسبه مقدار مالیات", description = "محاسبه مقدار مالیات براساس تاریخ شروع و همینطور پایان کاربر لاگین شده")
    @GetMapping("/calculate")
    public ResponseEntity<TaxPredictionEntity> calculate(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        TaxUser user = (TaxUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.calculateTaxPrediction(startDate, endDate, user));
    }
}