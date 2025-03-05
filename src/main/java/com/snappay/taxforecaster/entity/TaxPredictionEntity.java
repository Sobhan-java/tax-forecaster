package com.snappay.taxforecaster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    private BigDecimal salary;

    @Column
    private BigDecimal taxAmount;

    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    private UserEntity user;
}