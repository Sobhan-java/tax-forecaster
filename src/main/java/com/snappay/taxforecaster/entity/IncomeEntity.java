package com.snappay.taxforecaster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(name = "income")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncomeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private String description;

    @ManyToOne(targetEntity = UserEntity.class, optional = false)
    private UserEntity user;
}
