package com.challenge.challengetenpo.infrastructure.entrypoints.apirest.swagger;

import com.challenge.challengetenpo.domain.model.CallHistory;
import com.challenge.challengetenpo.domain.model.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "History", description = "Audit log of all API calls")
public interface HistoryApi {

    @Operation(
            summary = "Get paginated call history",
            description = "Returns a paginated and reverse-chronological list of all API calls, " +
                          "including endpoint, parameters, response or error, and timestamp"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    })
    ResponseEntity<PageResult<CallHistory>> getHistory(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Page size (1-100)", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size);
}
