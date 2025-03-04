package com.snappay.taxforecaster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "tax_prediction")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxPredictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private LocalDateTime periodStart;

    @Column
    private LocalDateTime periodEnd;

    @Column
    private BigDecimal predictedAmount;

    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    private UserEntity user;
}