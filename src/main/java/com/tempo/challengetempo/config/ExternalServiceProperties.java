package com.tempo.challengetempo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "external.service")
@Data
public class ExternalServiceProperties {
    private String baseUrl;
    private String percentagePath;
}