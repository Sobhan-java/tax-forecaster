package com.snappay.taxforecaster.service.taxprediction;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.controller.model.TaxPrediction;
import com.snappay.taxforecaster.entity.TaxRateEntity;
import com.snappay.taxforecaster.service.salary.SalaryService;
import com.snappay.taxforecaster.service.taxrate.TaxRateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;

@Service
public class TaxPredictionService {

    private final Cache<String, TaxPrediction> cache = Caffeine.newBuilder()
            .maximumSize(100000)
            .expireAfterAccess(Duration.ofSeconds(10))
            .build();
    private final TaxRateService taxRateService;
    private final SalaryService salaryService;

    public TaxPredictionService(TaxRateService taxRateService, SalaryService salaryService) {
        this.taxRateService = taxRateService;
        this.salaryService = salaryService;
    }

    public BigDecimal calculateTax(BigDecimal salary, TaxUser user) {
        if (null == salary) {
            throw new NotAcceptableException(Collections.singletonList("salary.is.null"));
        }

        List<TaxRateEntity> allSalaryTax = taxRateService.getAllTaxRate(salary);
        return allSalaryTax.stream()
                .map(taxRate -> null == taxRate.getMaxSalary() || taxRate.getMaxSalary().compareTo(salary) > 0
                        ? salary.subtract(taxRate.getMinSalary()).multiply(BigDecimal.valueOf(taxRate.getTaxRate()))
                        : taxRate.getMaxTax())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public TaxPrediction getTotalTaxAmount(LocalDateTime startDate, LocalDateTime endDate, TaxUser user) {
        if (null == startDate) {
            startDate = LocalDateTime.now().with(firstDayOfYear());
        }
        if (null == endDate) {
            endDate = LocalDateTime.now().with(lastDayOfYear());
        }
        String cacheKey = user.getId().concat("-").concat(startDate.toString()).concat("-").concat(endDate.toString());
        if (cache.estimatedSize() > 0 && null != cache.getIfPresent(cacheKey)) {
            return cache.getIfPresent(cacheKey);
        }

        BigDecimal totalSalary = salaryService.getTotalTaxAmount(user.getId(), startDate, endDate);
        BigDecimal totalTax = this.calculateTax(totalSalary, user);
        TaxPrediction taxPrediction = new TaxPrediction(totalTax, totalSalary);
        cache.put(cacheKey, taxPrediction);
        return taxPrediction;
    }
}
