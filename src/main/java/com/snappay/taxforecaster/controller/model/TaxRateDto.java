package com.snappay.taxforecaster.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxRateDto {

    private String id;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private Double taxRate;
}
