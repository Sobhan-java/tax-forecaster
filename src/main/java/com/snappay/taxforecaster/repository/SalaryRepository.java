package com.snappay.taxforecaster.repository;

import com.snappay.taxforecaster.entity.SalaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface SalaryRepository extends JpaRepository<SalaryEntity, String> {

    @Query("SELECT SUM (sal.amount) FROM tb_salary sal WHERE sal.user.id = :userId AND sal.startDate BETWEEN :startDate and :endDate AND sal.endDate BETWEEN :startDate and :endDate")
    BigDecimal getAllSalary(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}