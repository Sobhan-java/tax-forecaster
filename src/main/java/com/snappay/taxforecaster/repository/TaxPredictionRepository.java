package com.snappay.taxforecaster.repository;

import com.snappay.taxforecaster.entity.TaxPredictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TaxPredictionRepository extends JpaRepository<TaxPredictionEntity, String> {

    TaxPredictionEntity findByUserIdAndPeriodStartAndPeriodEnd(String userId, LocalDateTime startDate, LocalDateTime endDate);
}