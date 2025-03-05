package com.snappay.taxforecaster.service.salarytax;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.entity.TaxRateEntity;
import com.snappay.taxforecaster.model.TaxRateDto;
import com.snappay.taxforecaster.repository.TaxRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class TaxRateService {

    private final Cache<BigDecimal, List<TaxRateEntity>> cache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(2))
            .maximumSize(100000)
            .build();
    private final TaxRateRepository repository;

    public TaxRateService(TaxRateRepository repository) {
        this.repository = repository;
    }

    public List<TaxRateEntity> getAllTaxRate(BigDecimal salary) {
        if (cache.estimatedSize() > 0 && null != cache.getIfPresent(salary)) {
            return cache.getIfPresent(salary);
        }
        List<TaxRateEntity> allTaxRate = this.repository.getAllTaxRate(salary);
        cache.put(salary, allTaxRate);
        return allTaxRate;
    }

    public TaxRateEntity save(TaxRateDto dto, TaxUser user) {
        if (null == dto || null == dto.getTaxRate()) {
            throw new NotAcceptableException(Collections.singletonList("dto.is.not.complete"));
        }
        boolean salaryTaxExists = repository.checkSalaryTaxExists(dto.getMinSalary(), dto.getMaxSalary());
        if (salaryTaxExists) {
            throw new NotAcceptableException(Collections.singletonList("salary.tax.duplicated"));
        }
        BigDecimal maxTax = BigDecimal.ZERO;
        if (null != dto.getMaxSalary()) {
            if (dto.getMaxSalary().compareTo(dto.getMinSalary()) < 0) {
                throw new NotAcceptableException(Collections.singletonList("max.salary.is.less.than.min"));
            }
            maxTax = (dto.getMaxSalary().subtract(dto.getMinSalary())).multiply(this.getTaxPercentage(dto.getTaxRate()));
        }
        TaxRateEntity entity = new TaxRateEntity();
        entity.setMaxTax(maxTax);
        entity.setMinSalary(dto.getMinSalary());
        entity.setMaxSalary(dto.getMaxSalary());
        entity.setTaxRate(dto.getTaxRate());
        entity.setCreateDate(LocalDateTime.now());
        return repository.save(entity);
    }

    private BigDecimal getTaxPercentage(Double taxRate) {
        return BigDecimal.valueOf(taxRate / 100);
    }
}