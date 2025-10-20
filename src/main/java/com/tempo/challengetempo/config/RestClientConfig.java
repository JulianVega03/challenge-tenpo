package com.tempo.challengetempo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final ExternalServiceProperties externalServiceProperties;

    @Bean
    public RestClient externalRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(externalServiceProperties.getBaseUrl())
                .build();
    }

}