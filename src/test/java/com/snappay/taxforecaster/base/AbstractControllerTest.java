package com.snappay.taxforecaster.base;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.snappay.taxforecaster.TaxForecasterApplication;
import com.snappay.taxforecaster.controller.model.TaxUserDto;
import com.snappay.taxforecaster.controller.model.TokenModel;
import com.snappay.taxforecaster.service.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Getter
@ActiveProfiles(profiles = {"test"})
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {TaxForecasterApplication.class})
public class AbstractControllerTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
    private final String baseUrl = "/app/oauth";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    private String token;

    @PostConstruct
    public void postConstruct() {
        try {
            TaxUserDto dto = new TaxUserDto();
            dto.setUsername("admin");
            dto.setPassword("pass");
            if (null == userService.getOne(dto.getUsername())) {
                this.registerUser(dto);
            }
            String contentAsString = mockMvc.perform(post(baseUrl + "/login")
                            .content(OBJECT_MAPPER.writeValueAsString(dto))
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is(HttpStatus.OK.value())).andReturn().getResponse().getContentAsString();
            TokenModel tokenModel = OBJECT_MAPPER.readValue(contentAsString, TokenModel.class);
            this.token = "Bearer " + tokenModel.getTokenValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registerUser(TaxUserDto dto) throws Exception {
        mockMvc.perform(post(baseUrl + "/register")
                        .content(OBJECT_MAPPER.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
