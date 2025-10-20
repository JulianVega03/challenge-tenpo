package com.tempo.challengetempo.controllers.mock;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Tag(name = "Mocks Externos", description = "Endpoints para simular servicios externos.")
@RestController
@RequestMapping("/mock")
public class MockExternalPercentageController {

    @Operation(summary = "Simula la obtención de un porcentaje externo.",
            description = "Devuelve un valor de porcentaje aleatorio (entre 10 y 20) para simular el servicio de un tercero.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Simulación exitosa.",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/external/percentage")
    public ResponseEntity<Map<String, Object>> getExternalPercentage() {
        double percentage = ThreadLocalRandom.current().nextDouble(10, 20);
        return ResponseEntity.ok(Map.of(
                "percentage", percentage,
                "source", "mock-service"
        ));
    }

}
