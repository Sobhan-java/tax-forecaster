package com.snappay.taxforecaster.service.taxrate;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.controller.model.TaxRateDto;
import com.snappay.taxforecaster.entity.TaxRateEntity;
import com.snappay.taxforecaster.repository.TaxRateRepository;
import org.apache.commons.lang3.StringUtils;
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
        this.validationDto(dto);
        TaxRateEntity entity = new TaxRateEntity();
        entity.setCreateDate(LocalDateTime.now());
        this.convert(dto, entity);
        return repository.save(entity);
    }

    public TaxRateEntity update(TaxRateDto dto, TaxUser user) {
        this.validationDto(dto);
        if (StringUtils.isBlank(dto.getId())) {
            throw new NotAcceptableException(Collections.singletonList("id.is.null"));
        }
        TaxRateEntity entity = repository.findById(dto.getId()).orElseThrow(() -> new NotAcceptableException(Collections.singletonList("tax.rate.not.found")));
        this.convert(dto, entity);
        return repository.save(entity);
    }

    private void convert(TaxRateDto dto, TaxRateEntity entity) {
        entity.setMinSalary(dto.getMinSalary());
        entity.setMaxSalary(dto.getMaxSalary());
        entity.setMaxTax(this.getMaxTax(dto.getMaxSalary(), dto.getMinSalary(), dto.getTaxRate()));
        entity.setTaxRate(dto.getTaxRate() / 100);
    }

    private BigDecimal getMaxTax(BigDecimal maxSalary, BigDecimal minSalary, Double taxRate) {
        if (null == maxSalary) {
            return BigDecimal.ZERO;
        }
        return (maxSalary.subtract(minSalary))
                .multiply(this.getTaxPercentage(taxRate));
    }

    private void validationDto(TaxRateDto dto) {
        if (null == dto) {
            throw new NotAcceptableException(Collections.singletonList("dto.is.not.complete"));
        }
        if (null == dto.getTaxRate()) {
            throw new NotAcceptableException(Collections.singletonList("tax.rate.is.null"));
        }
        if (null == dto.getMinSalary()) {
            throw new NotAcceptableException(Collections.singletonList("min.salary.is.null"));
        }
        if (null != dto.getMaxSalary() && dto.getMaxSalary().compareTo(dto.getMinSalary()) < 0) {
            throw new NotAcceptableException(Collections.singletonList("max.salary.is.less.than.min"));
        }
        boolean salaryTaxExists = repository.checkSalaryTaxExists(dto.getMinSalary(), dto.getMaxSalary(), dto.getId());
        if (salaryTaxExists) {
            throw new NotAcceptableException(Collections.singletonList("salary.tax.duplicated"));
        }
    }

    private BigDecimal getTaxPercentage(Double taxRate) {
        return BigDecimal.valueOf(taxRate / 100);
    }
}