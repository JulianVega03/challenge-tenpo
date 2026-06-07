package com.challenge.challengetenpo.infrastructure.adapters.externalpercentage;

import com.challenge.challengetenpo.domain.model.exception.ExternalServiceException;
import com.challenge.challengetenpo.domain.model.gateway.PercentageGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestClientPercentageGateway implements PercentageGateway {

    private final RestClient restClient;
    private final PercentageProperties properties;

    @Override
    @Retryable(
            retryFor = {ResourceAccessException.class, RestClientResponseException.class},
            maxAttemptsExpression = "${retry.max-attempts:3}",
            backoff = @Backoff(delayExpression = "${retry.backoff-delay:300}")
    )
    public BigDecimal getPercentage() {
        log.debug("Fetching percentage from: {}", properties.url());
        PercentageResponse response = restClient.get()
                .uri(properties.url())
                .retrieve()
                .body(PercentageResponse.class);
        if (response == null || response.percentage() == null) {
            throw new ExternalServiceException("Empty response from percentage service", null);
        }
        return response.percentage();
    }

    @Recover
    public BigDecimal recover(Exception ex) {
        log.error("Percentage service unavailable after 3 retries: {}", ex.getMessage());
        throw new ExternalServiceException("Percentage service unavailable after 3 retries", ex);
    }
}
