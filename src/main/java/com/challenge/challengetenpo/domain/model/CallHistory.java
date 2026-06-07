package com.challenge.challengetenpo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record CallHistory(
        UUID id,
        LocalDateTime timestamp,
        String endpoint,
        String method,
        String parameters,
        String response,
        String errorMessage,
        int httpStatus
) {}
