package com.snappay.taxforecaster.repository;

import com.snappay.taxforecaster.entity.TaxPredictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TaxPredictionRepository extends JpaRepository<TaxPredictionEntity, String> {

    TaxPredictionEntity findByUserIdAndSalary(String userId, BigDecimal salary);
}