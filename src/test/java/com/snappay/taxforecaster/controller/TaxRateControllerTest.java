package com.snappay.taxforecaster.controller;

import com.snappay.taxforecaster.base.AbstractControllerTest;
import com.snappay.taxforecaster.controller.model.TaxRateDto;
import com.snappay.taxforecaster.entity.TaxRateEntity;
import com.snappay.taxforecaster.repository.TaxRateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class TaxRateControllerTest extends AbstractControllerTest {

    @Autowired
    private TaxRateRepository taxRateRepository;

    @Test
    void testSave() throws Exception {
        TaxRateDto dto = new TaxRateDto();
        dto.setMinSalary(BigDecimal.valueOf(1440000000));
        dto.setMaxSalary(BigDecimal.valueOf(1970000000));
        dto.setTaxRate(10.0);
        dto.setId(UUID.randomUUID().toString());

        String contentAsString = super.getMockMvc().perform(post("/api/tax-rate")
                        .content(OBJECT_MAPPER.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, super.getToken()))
                .andExpect(status().is(HttpStatus.OK.value())).andReturn().getResponse().getContentAsString();
        String id = OBJECT_MAPPER.readValue(contentAsString, TaxRateEntity.class).getId();
        TaxRateEntity entity = taxRateRepository.findById(id).orElse(null);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(dto.getTaxRate() / 100, entity.getTaxRate());
        Assertions.assertEquals(dto.getMinSalary(), entity.getMinSalary().setScale(0));
        Assertions.assertEquals(dto.getMaxSalary(), entity.getMaxSalary().setScale(0));

        taxRateRepository.deleteAll();
    }
}