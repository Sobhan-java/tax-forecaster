package com.snappay.taxforecaster.service.salary;

import com.snappay.taxforecaster.common.TaxUser;
import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.controller.model.SalaryDto;
import com.snappay.taxforecaster.entity.SalaryEntity;
import com.snappay.taxforecaster.repository.SalaryRepository;
import com.snappay.taxforecaster.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
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
        this.validationDto(dto);
        SalaryEntity entity = new SalaryEntity();
        entity.setCreateDate(LocalDateTime.now());
        entity.setAmount(dto.getAmount());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setDescription(dto.getDescription());
        entity.setUser(userService.getOne(user.getUsername()));
        return repository.save(entity);
    }

    public SalaryEntity update(SalaryDto dto, TaxUser user) {
        if (StringUtils.isBlank(dto.getId())) {
            throw new NotAcceptableException(Collections.singletonList("id.is.null"));
        }
        this.validationDto(dto);
        SalaryEntity entity = repository.findById(dto.getId()).orElseThrow(() -> new NotAcceptableException(Collections.singletonList("salary.not.found")));
        if (!entity.getUser().getId().equals(user.getId())) {
            throw new NotAcceptableException(Collections.singletonList("salary.not.found"));
        }
        entity.setAmount(dto.getAmount());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setDescription(dto.getDescription());
        return repository.save(entity);
    }

    private void validationDto(SalaryDto dto) {
        if (null == dto) {
            throw new NotAcceptableException(Collections.singletonList("entry.dto.is.not.complete"));
        }
        if (null == dto.getAmount()) {
            throw new NotAcceptableException(Collections.singletonList("amount.is.null"));
        }
        if (null == dto.getStartDate()) {
            throw new NotAcceptableException(Collections.singletonList("start.date.is.null"));
        }
        if (null == dto.getEndDate()) {
            throw new NotAcceptableException(Collections.singletonList("end.date.is.null"));
        }
        if (dto.getEndDate().compareTo(dto.getStartDate()) < 0) {
            throw new NotAcceptableException(Collections.singletonList("end.date.is.less.than.start"));
        }
    }

    public BigDecimal getAllSalary(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return repository.getAllSalary(userId, startDate, endDate);
    }
}