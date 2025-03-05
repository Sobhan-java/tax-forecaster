package com.snappay.taxforecaster.service.taxprediction;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.entity.TaxPredictionEntity;
import com.snappay.taxforecaster.entity.TaxRateEntity;
import com.snappay.taxforecaster.model.TaxPrediction;
import com.snappay.taxforecaster.repository.TaxPredictionRepository;
import com.snappay.taxforecaster.service.salarytax.TaxRateService;
import com.snappay.taxforecaster.service.user.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class TaxPredictionService {

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
        TaxPredictionEntity entity = repository.findByUserIdAndSalary(user.getId(), salary);
        if (null != entity) {
            return new TaxPrediction(entity.getTaxAmount(), entity.getSalary());
        }

        BigDecimal taxAmount = BigDecimal.valueOf(0);
        List<TaxRateEntity> allSalaryTax = taxRateService.getAllTaxRate(salary);
        for (TaxRateEntity salaryTax : allSalaryTax) {
            if (BigDecimal.valueOf(0).equals(salaryTax.getMaxSalary()) || salaryTax.getMaxSalary().compareTo(salary) > 0) {
                taxAmount = taxAmount.add(salary.subtract(salaryTax.getMinSalary()));
            } else {
                taxAmount = taxAmount.add(salaryTax.getMaxTax());
            }
        }
        entity = new TaxPredictionEntity();
        entity.setTaxAmount(taxAmount);
        entity.setSalary(salary);
        entity.setUser(userService.getOne(user.getUsername()));
        repository.save(entity);
        return new TaxPrediction(entity.getTaxAmount(), entity.getSalary());
    }
}
