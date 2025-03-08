package com.snappay.taxforecaster.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.snappay.taxforecaster.base.AbstractControllerTest;
import com.snappay.taxforecaster.controller.model.TaxPrediction;
import com.snappay.taxforecaster.entity.TaxRateEntity;
import com.snappay.taxforecaster.repository.TaxRateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class TaxPredictionControllerTest extends AbstractControllerTest {

    @Autowired
    private TaxRateRepository taxRateRepository;

    @Test
    void testCalculate() throws Exception {
        this.dataProvider();
        BigDecimal salary = BigDecimal.valueOf(1970000002);
        String responseAsString = super.getMockMvc().perform(get("/api/tax-prediction/calculate")
                        .param("salary", salary.toString())
                        .header(HttpHeaders.AUTHORIZATION, super.getToken()))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        TaxPrediction taxPrediction = OBJECT_MAPPER.readValue(responseAsString, new TypeReference<>() {
        });
        Assertions.assertNotNull(taxPrediction);
        Assertions.assertEquals(taxPrediction.getTaxAmount().stripTrailingZeros(), BigDecimal.valueOf(53000000.2));
        Assertions.assertEquals(taxPrediction.getSalary(), salary);

        taxRateRepository.deleteAll();
    }

    private void dataProvider() {
        TaxRateEntity firstTaxRate = new TaxRateEntity();
        firstTaxRate.setCreateDate(LocalDateTime.now());
        firstTaxRate.setMinSalary(BigDecimal.valueOf(1440000000));
        firstTaxRate.setMaxSalary(BigDecimal.valueOf(1970000000));
        firstTaxRate.setTaxRate(0.1);
        firstTaxRate.setMaxTax((firstTaxRate.getMaxSalary().subtract(firstTaxRate.getMinSalary()))
                .multiply(BigDecimal.valueOf(firstTaxRate.getTaxRate())));
        taxRateRepository.save(firstTaxRate);

        TaxRateEntity secondTaxRate = new TaxRateEntity();
        secondTaxRate.setCreateDate(LocalDateTime.now());
        secondTaxRate.setMinSalary(BigDecimal.valueOf(1970000001));
        secondTaxRate.setMaxSalary(BigDecimal.valueOf(3240000000.00));
        secondTaxRate.setTaxRate(0.2);
        secondTaxRate.setMaxTax((secondTaxRate.getMaxSalary().subtract(secondTaxRate.getMinSalary()))
                .multiply(BigDecimal.valueOf(secondTaxRate.getTaxRate())));
        taxRateRepository.save(secondTaxRate);
    }
}