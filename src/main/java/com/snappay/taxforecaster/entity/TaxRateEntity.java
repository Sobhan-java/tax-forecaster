package com.snappay.taxforecaster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "tb_tax_rate")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private BigDecimal minSalary;

    @Column
    private BigDecimal maxSalary;

    @Column(nullable = false)
    private BigDecimal maxTax;

    @Column(unique = true)
    private Double taxRate;
}
