package com.snappay.taxforecaster.repository;

import com.snappay.taxforecaster.entity.TaxRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TaxRateRepository extends JpaRepository<TaxRateEntity, String> {

    @Query("SELECT taxrate FROM tax_rate taxrate WHERE taxrate.minSalary < :salary OR taxrate.maxSalary < :salary")
    List<TaxRateEntity> getAllTaxRate(BigDecimal salary);

    @Query("SELECT COUNT(taxrate) > 0 FROM tax_rate taxrate where taxrate.minSalary <= :maxSalary AND taxrate.maxSalary > :minSalary AND taxrate.id not like :id")
    boolean checkSalaryTaxExists(BigDecimal minSalary, BigDecimal maxSalary, String id);
}
