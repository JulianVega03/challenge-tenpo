package com.challenge.challengetenpo.infrastructure.adapters.externalpercentage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient restClient(PercentageProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(properties.connectTimeout()));
        factory.setReadTimeout(Duration.ofMillis(properties.readTimeout()));
        return RestClient.builder().requestFactory(factory).build();
    }
}
