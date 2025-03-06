package com.snappay.taxforecaster.controller;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.controller.model.SalaryDto;
import com.snappay.taxforecaster.entity.SalaryEntity;
import com.snappay.taxforecaster.service.salary.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    private final SalaryService service;

    public SalaryController(SalaryService service) {
        this.service = service;
    }

    @Operation(summary = "ثبت درآمد کاربر")
    @PostMapping(value = "")
    public ResponseEntity<SalaryEntity> save(@RequestBody SalaryDto dto) {
        TaxUser user = (TaxUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.save(dto, user));
    }

    @Operation(summary = "ویرایش درآمد کاربر")
    @PutMapping(value = "")
    public ResponseEntity<SalaryEntity> update(@RequestBody SalaryDto dto) {
        TaxUser user = (TaxUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.update(dto, user));
    }
}
