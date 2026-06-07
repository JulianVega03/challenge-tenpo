package com.challenge.challengetenpo.infrastructure.entrypoints.apirest.event;

import java.time.LocalDateTime;

public record CallHistoryEvent(
        String endpoint,
        String method,
        String parameters,
        String response,
        String errorMessage,
        int httpStatus,
        LocalDateTime timestamp
) {}
