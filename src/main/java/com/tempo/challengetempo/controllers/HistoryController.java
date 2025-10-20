package com.tempo.challengetempo.controllers;

import com.tempo.challengetempo.dtos.CallHistoryResponseDto;
import com.tempo.challengetempo.serivces.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Historial", description = "Servicio para consultar el historial de llamadas a la API.")
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @Operation(summary = "Obtiene el historial de llamadas registradas.",
            description = "Retorna una lista paginada de todas las operaciones realizadas, ordenadas por fecha descendente por defecto.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consulta de historial exitosa",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Parámetro de paginación o ordenamiento inválido."),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<Page<CallHistoryResponseDto>> getHistory(Pageable pageable) {
        Page<CallHistoryResponseDto> history = historyService.getHistory(pageable);
        return ResponseEntity.ok(history);
    }

}
