package com.tempo.challengetempo.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CallHistoryResponseDto(
        String endpoint,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime date,
        String parameters,
        String response,
        String error
) {}