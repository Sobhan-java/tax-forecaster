package com.snappay.taxforecaster.controller;

import com.snappay.taxforecaster.base.AbstractControllerTest;
import com.snappay.taxforecaster.controller.model.SalaryDto;
import com.snappay.taxforecaster.entity.SalaryEntity;
import com.snappay.taxforecaster.repository.SalaryRepository;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SalaryControllerTest extends AbstractControllerTest {

    @Autowired
    private SalaryRepository salaryRepository;

    @SneakyThrows
    @Test
    void save() {
        BigDecimal salary = BigDecimal.valueOf(1970000002);
        SalaryDto dto = new SalaryDto();
        dto.setAmount(salary);
        dto.setDescription(RandomStringUtils.randomAlphabetic(15));
        dto.setStartDate(LocalDateTime.now().minusDays(2));
        dto.setEndDate(LocalDateTime.now().plusDays(10));
        String token = super.getToken();
        String contentAsString = super.getMockMvc().perform(MockMvcRequestBuilders.post("/api/salary")
                        .content(OBJECT_MAPPER.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().is(HttpStatus.OK.value())).andReturn().getResponse().getContentAsString();
        String id = OBJECT_MAPPER.readValue(contentAsString, SalaryEntity.class).getId();
        SalaryEntity entity = salaryRepository.findById(id).orElse(null);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(entity.getAmount().setScale(0), salary);

        salaryRepository.deleteById(id);
    }
}