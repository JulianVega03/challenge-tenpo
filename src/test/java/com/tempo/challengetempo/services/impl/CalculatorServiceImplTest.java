package com.tempo.challengetempo.services.impl;

import com.tempo.challengetempo.serivces.CalculatorService;
import com.tempo.challengetempo.serivces.PercentageService;
import com.tempo.challengetempo.serivces.impl.CalculatorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculatorServiceImplTest {

    @InjectMocks
    private CalculatorServiceImpl calculatorService;

    @Mock
    private PercentageService percentageService;

    private static final double PERCENTAGE_VALUE = 10.0; // 10%

    @BeforeEach
    void setUp() {
        when(percentageService.getPercentage()).thenReturn(PERCENTAGE_VALUE);
    }

    @Test
    void calculateSum_shouldApplyPercentageCorrectly() {
        double number1 = 100.0;
        double number2 = 50.0;

        double expectedResult = 165.0;

        double actualResult = calculatorService.calculateSum(number1, number2);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void calculateSum_shouldReturnBaseSumWhenPercentageIsZero() {
        double number1 = 75.0;
        double number2 = 25.0;
        double expectedResult = 100.0;

        when(percentageService.getPercentage()).thenReturn(0.0);

        double actualResult = calculatorService.calculateSum(number1, number2);

        assertEquals(expectedResult, actualResult);
    }
}
