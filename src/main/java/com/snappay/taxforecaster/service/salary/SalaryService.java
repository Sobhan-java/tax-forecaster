package com.snappay.taxforecaster.service.salary;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.controller.model.SalaryDto;
import com.snappay.taxforecaster.entity.SalaryEntity;
import com.snappay.taxforecaster.repository.SalaryRepository;
import com.snappay.taxforecaster.service.user.UserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class SalaryService {
    private final SalaryRepository repository;
    private final UserService userService;

    public SalaryService(SalaryRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public SalaryEntity save(SalaryDto dto, TaxUser user) {
        if (null == dto || null == dto.getAmount()) {
            throw new NotAcceptableException(Collections.singletonList("entry.dto.is.not.complete"));
        }
        boolean existsIncome = repository.checkSalaryExists(dto.getStartDate(), dto.getEndDate(), user.getUsername());
        if (existsIncome) {
            throw new NotAcceptableException(Collections.singletonList("income.is.duplicated"));
        }

        SalaryEntity entity = new SalaryEntity();
        entity.setCreateDate(LocalDateTime.now());
        entity.setAmount(dto.getAmount());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setDescription(dto.getDescription());
        entity.setUser(userService.getOne(user.getUsername()));
        return repository.save(entity);
    }

    public BigDecimal getTotalTaxAmount(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return repository.getAllSalary(userId, startDate, endDate);
    }
}