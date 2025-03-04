package com.snappay.taxforecaster.repository;

import com.snappay.taxforecaster.entity.IncomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity, String> {

    @Query("SELECT COALESCE(SUM (inc.amount),0) FROM income inc WHERE inc.user.id = :userId AND inc.createDate BETWEEN :startDate AND :endDate")
    BigDecimal getAllAmountOfUser(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(inc) > 0 FROM income inc where inc.endDate >= :startDate AND inc.startDate <= :endDate AND inc.user.id = :userId")
    boolean checkIncomeExists(LocalDateTime startDate, LocalDateTime endDate, String userId);
}
