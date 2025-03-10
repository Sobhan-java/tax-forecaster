package com.snappay.taxforecaster.controller;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.controller.model.TaxRateDto;
import com.snappay.taxforecaster.entity.TaxRateEntity;
import com.snappay.taxforecaster.service.taxrate.TaxRateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tax-rate")
public class TaxRateController {

    private final TaxRateService service;

    public TaxRateController(TaxRateService service) {
        this.service = service;
    }

    @Operation(summary = "ثبت جدول مالیاتی", description = "باید مشخص کنید که از حداقل حقوق و تا حداکثر حقوق های مختلف چه نرخ مالیاتی ثبت شود")
    @PostMapping(value = "")
    public ResponseEntity<TaxRateEntity> save(@RequestBody TaxRateDto dto) {
        TaxUser user = (TaxUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.save(dto, user));
    }

    @Operation(summary = "ویرایش جدول مالیاتی")
    @PutMapping(value = "")
    public ResponseEntity<TaxRateEntity> update(@RequestBody TaxRateDto dto) {
        TaxUser user = (TaxUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.update(dto, user));
    }
}
