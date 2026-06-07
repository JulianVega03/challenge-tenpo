package com.challenge.challengetenpo.infrastructure.entrypoints.apirest.swagger;

import com.challenge.challengetenpo.infrastructure.entrypoints.apirest.dto.CalculationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Tag(name = "Calculation", description = "Arithmetic operations with dynamic external percentage")
public interface CalculationApi {

    @Operation(
            summary = "Calculate sum with dynamic percentage",
            description = "Sums two numbers and applies a percentage fetched from an external service. " +
                          "Formula: result = (num1 + num2) * (1 + percentage / 100)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Calculation successful",
                    content = @Content(schema = @Schema(implementation = CalculationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing parameters", content = @Content),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded (max 3 RPM)", content = @Content),
            @ApiResponse(responseCode = "503", description = "External percentage service unavailable", content = @Content)
    })
    ResponseEntity<CalculationResponse> calculate(
            @Parameter(description = "First operand", required = true, example = "5")
            @RequestParam @NotNull @DecimalMin("-1000000") @DecimalMax("1000000") BigDecimal num1,
            @Parameter(description = "Second operand", required = true, example = "5")
            @RequestParam @NotNull @DecimalMin("-1000000") @DecimalMax("1000000") BigDecimal num2);
}
