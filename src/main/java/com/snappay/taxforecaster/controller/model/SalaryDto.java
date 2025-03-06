package com.snappay.taxforecaster.controller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalaryDto {

    private String id;
    private BigDecimal amount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
}