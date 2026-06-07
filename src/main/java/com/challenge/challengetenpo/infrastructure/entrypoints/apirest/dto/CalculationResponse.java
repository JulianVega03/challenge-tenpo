package com.challenge.challengetenpo.infrastructure.entrypoints.apirest.dto;

import com.challenge.challengetenpo.domain.model.Calculation;

import java.math.BigDecimal;

public record CalculationResponse(BigDecimal num1, BigDecimal num2, BigDecimal percentage, BigDecimal result) {

    public static CalculationResponse from(Calculation calculation) {
        return new CalculationResponse(
                calculation.num1(),
                calculation.num2(),
                calculation.percentage(),
                calculation.result()
        );
    }
}
