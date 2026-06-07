package com.challenge.challengetenpo.infrastructure.adapters.externalpercentage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "external.percentage")
public record PercentageProperties(
        String url,
        int connectTimeout,
        int readTimeout
) {}
