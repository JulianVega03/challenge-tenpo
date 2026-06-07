package com.challenge.challengetenpo.domain.usecase;

import com.challenge.challengetenpo.domain.model.Calculation;
import com.challenge.challengetenpo.domain.model.gateway.PercentageGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class CalculateWithPercentageUseCase {

    private final PercentageGateway percentageGateway;

    public Calculation calculate(BigDecimal num1, BigDecimal num2) {
        BigDecimal percentage = percentageGateway.getPercentage();
        BigDecimal sum = num1.add(num2);
        BigDecimal factor = percentage.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal result = sum.add(sum.multiply(factor)).setScale(2, RoundingMode.HALF_UP);
        return new Calculation(num1, num2, percentage, result);
    }
}
