package com.tempo.challengetempo.controllers;

import com.tempo.challengetempo.serivces.CalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
public class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CalculatorService calculatorService;

    private static final String BASE_URL = "/api/calculator/sum";
    private static final double TEST_RESULT = 165.50;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CalculatorService calculatorService() {
            return mock(CalculatorService.class);
        }
    }

    @Test
    void calculateSum_shouldReturnOkAndResult_whenInputsAreValid() throws Exception {
        double number1 = 100.0;
        double number2 = 50.0;

        when(calculatorService.calculateSum(number1, number2)).thenReturn(TEST_RESULT);

        mockMvc.perform(get(BASE_URL)
                        .param("number1", String.valueOf(number1))
                        .param("number2", String.valueOf(number2)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(TEST_RESULT)));
    }

    @Test
    void calculateSum_shouldReturnBadRequest_whenNumber1IsMissing() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("number2", "50.0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateSum_shouldReturnBadRequest_whenNumber2IsInvalidType() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("number1", "100.0")
                        .param("number2", "not_a_number"))
                .andExpect(status().isBadRequest());
    }

}