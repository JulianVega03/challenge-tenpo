package com.tempo.challengetempo.controllers;

import com.tempo.challengetempo.serivces.CalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Calculadora", description = "Servicio de suma que aplica un porcentaje dinámico.")
@RestController()
@RequestMapping("/api/calculator")
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;

    @Operation(summary = "Calcula la suma de dos números aplicando un porcentaje externo",
            description = "Suma 'number1' y 'number2', luego añade un porcentaje obtenido de un servicio externo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa",
                    content = @Content(schema = @Schema(implementation = Double.class))),
            @ApiResponse(responseCode = "400", description = "Parámetro inválido o faltante. (Ej. 'not_a_number' o falta un parámetro)"),
            @ApiResponse(responseCode = "503", description = "Servicio externo no disponible o caché vacía. Se puede devolver si falla la obtención del porcentaje."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/sum")
    public ResponseEntity<Double> calculateSum(@RequestParam double number1,
                                               @RequestParam double number2) {
        return ResponseEntity.ok(calculatorService.calculateSum(number1, number2));
    }

}
