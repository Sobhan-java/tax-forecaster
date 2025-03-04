package com.snappay.taxforecaster.service.taxprediction;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.entity.TaxPredictionEntity;
import com.snappay.taxforecaster.repository.IncomeRepository;
import com.snappay.taxforecaster.repository.TaxPredictionRepository;
import com.snappay.taxforecaster.service.user.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class TaxPredictionService {

    private final TaxPredictionRepository repository;
    private final UserService userService;
    private final IncomeRepository incomeRepository;
    private final Long taxPercentage;

    public TaxPredictionService(TaxPredictionRepository repository, UserService userService, IncomeRepository incomeRepository, @Value("${tax.percentage.amount}") Long taxPercentage) {
        this.repository = repository;
        this.userService = userService;
        this.incomeRepository = incomeRepository;
        this.taxPercentage = taxPercentage;
    }

    @Cacheable(value = "taxPrediction", key = "#user.username + #start + #end")
    public TaxPredictionEntity calculateTaxPrediction(String start, String end, TaxUser user) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        if (startDate.isBefore(endDate)) {
            throw new NotAcceptableException(Collections.singletonList("start.date.is.before.end"));
        }
        TaxPredictionEntity taxPrediction = repository.findByUserIdAndPeriodStartAndPeriodEnd(user.getId(), startDate, endDate);
        if (null != taxPrediction) {
            return taxPrediction;
        }
        BigDecimal incomeAmount = incomeRepository.getAllAmountOfUser(user.getId(), startDate, endDate);
        incomeAmount = incomeAmount.multiply(this.getTaxPercentage());

        taxPrediction = new TaxPredictionEntity();
        taxPrediction.setPeriodStart(startDate);
        taxPrediction.setPeriodEnd(endDate);
        taxPrediction.setPredictedAmount(incomeAmount);
        taxPrediction.setUser(userService.getOne(user.getUsername()));
        return repository.save(taxPrediction);
    }

    private BigDecimal getTaxPercentage() {
        return BigDecimal.valueOf(taxPercentage * 100);
    }
}
