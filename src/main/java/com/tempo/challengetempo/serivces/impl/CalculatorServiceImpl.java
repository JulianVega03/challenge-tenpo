package com.tempo.challengetempo.serivces.impl;

import com.tempo.challengetempo.serivces.CalculatorService;
import com.tempo.challengetempo.serivces.PercentageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculatorServiceImpl implements CalculatorService {

    private final PercentageService percentageService;

    @Override
    public double calculateSum(double number1, double number2) {
        double baseSum = number1 + number2;
        double percentage = percentageService.getPercentage();
        return baseSum + (baseSum * (percentage / 100));
    }

}

