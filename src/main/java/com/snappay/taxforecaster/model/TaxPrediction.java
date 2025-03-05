package com.snappay.taxforecaster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxPrediction {

    private BigDecimal taxAmount;
    private BigDecimal salary;
}
