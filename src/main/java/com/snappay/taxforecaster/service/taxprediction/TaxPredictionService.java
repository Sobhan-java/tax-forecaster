package com.snappay.taxforecaster.service.taxprediction;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.entity.TaxPredictionEntity;
import com.snappay.taxforecaster.entity.TaxRateEntity;
import com.snappay.taxforecaster.model.TaxPrediction;
import com.snappay.taxforecaster.repository.TaxPredictionRepository;
import com.snappay.taxforecaster.service.salarytax.TaxRateService;
import com.snappay.taxforecaster.service.user.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class TaxPredictionService {

    private final Cache<BigDecimal, TaxPrediction> cache = Caffeine.newBuilder()
            .maximumSize(100000)
            .expireAfterAccess(Duration.ofMinutes(40))
            .build();
    private final TaxPredictionRepository repository;
    private final UserService userService;
    private final TaxRateService taxRateService;

    public TaxPredictionService(TaxPredictionRepository repository, UserService userService, TaxRateService taxRateService) {
        this.repository = repository;
        this.userService = userService;
        this.taxRateService = taxRateService;
    }

    public TaxPrediction calculateTaxPrediction(BigDecimal salary, TaxUser user) {
        if (null == salary) {
            throw new NotAcceptableException(Collections.singletonList("salary.is.null"));
        }
        if (cache.estimatedSize() > 0 && null != cache.getIfPresent(salary)) {
            return cache.getIfPresent(salary);
        }

        TaxPrediction taxPrediction = repository.findByUserIdAndSalary(user.getId(), salary)
                .map(entity -> new TaxPrediction(entity.getTaxAmount(), entity.getSalary()))
                .orElseGet(() -> calculateAndSaveTaxPrediction(salary, user));
        cache.put(salary, taxPrediction);
        return taxPrediction;
    }

    private TaxPrediction calculateAndSaveTaxPrediction(BigDecimal salary, TaxUser user) {
        List<TaxRateEntity> allSalaryTax = taxRateService.getAllTaxRate(salary);
        BigDecimal taxAmount = allSalaryTax.stream()
                .map(salaryTax -> BigDecimal.ZERO.compareTo(salaryTax.getMaxTax()) == 0 || salaryTax.getMaxSalary().compareTo(salary) > 0
                        ? salary.subtract(salaryTax.getMinSalary())
                        : salaryTax.getMaxTax())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.saveTaxPredictionAsync(salary, taxAmount, user);
        return new TaxPrediction(taxAmount, salary);
    }

    @Async
    public void saveTaxPredictionAsync(BigDecimal salary, BigDecimal taxAmount, TaxUser user) {
        TaxPredictionEntity entity = new TaxPredictionEntity();
        entity.setTaxAmount(taxAmount);
        entity.setSalary(salary);
        entity.setUser(userService.getOne(user.getUsername()));
        repository.save(entity);
    }
}
