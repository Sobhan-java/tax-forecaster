package com.snappay.taxforecaster.service.income;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.entity.IncomeEntity;
import com.snappay.taxforecaster.model.IncomeDto;
import com.snappay.taxforecaster.repository.IncomeRepository;
import com.snappay.taxforecaster.service.user.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class IncomeService {

    private final IncomeRepository repository;
    private final UserService userService;

    public IncomeService(IncomeRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public IncomeEntity save(IncomeDto dto, TaxUser user) {
        if (null == dto || null == dto.getAmount()) {
            throw new NotAcceptableException(Collections.singletonList("entry.dto.is.not.complete"));
        }
        boolean existsIncome = repository.checkIncomeExists(dto.getStartDate(), dto.getEndDate(), user.getUsername());
        if (existsIncome) {
            throw new NotAcceptableException(Collections.singletonList("income.is.duplicated"));
        }

        IncomeEntity entity = new IncomeEntity();
        entity.setCreateDate(LocalDateTime.now());
        entity.setAmount(dto.getAmount());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setDescription(dto.getDescription());
        entity.setUser(userService.getOne(user.getUsername()));
        return repository.save(entity);
    }
}