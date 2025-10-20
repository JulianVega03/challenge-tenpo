package com.tempo.challengetempo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "cache")
public class CacheProperties {
    private long expirationTime;
    private String expirationTimeUnit;
}
