package com.challenge.challengetenpo.infrastructure.entrypoints.apirest;

import com.challenge.challengetenpo.domain.usecase.CalculateWithPercentageUseCase;
import com.challenge.challengetenpo.infrastructure.entrypoints.apirest.dto.CalculationResponse;
import com.challenge.challengetenpo.infrastructure.entrypoints.apirest.swagger.CalculationApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CalculationController implements CalculationApi {

    private final CalculateWithPercentageUseCase calculateWithPercentageUseCase;

    @Override
    @PostMapping("/calculate")
    public ResponseEntity<CalculationResponse> calculate(
            @RequestParam BigDecimal num1,
            @RequestParam BigDecimal num2) {
        return ResponseEntity.ok(
                CalculationResponse.from(calculateWithPercentageUseCase.calculate(num1, num2))
        );
    }
}
