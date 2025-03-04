package com.snappay.taxforecaster.controller;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.entity.IncomeEntity;
import com.snappay.taxforecaster.model.IncomeDto;
import com.snappay.taxforecaster.service.income.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    private final IncomeService service;

    public IncomeController(IncomeService service) {
        this.service = service;
    }

    @Operation(summary = "ثبت درآمد کاربر", description = "درصورتیکه تاریخ ایجاد یا انتهای وارد شده برای کاربر ثبت شده باشد ارور میدهد")
    @PostMapping(value = "")
    public ResponseEntity<IncomeEntity> save(@RequestBody IncomeDto dto) {
        TaxUser user = (TaxUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(service.save(dto, user));
    }
}
