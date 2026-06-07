package com.challenge.challengetenpo.applications.config;

import com.challenge.challengetenpo.domain.model.gateway.CallHistoryGateway;
import com.challenge.challengetenpo.domain.model.gateway.PercentageGateway;
import com.challenge.challengetenpo.domain.usecase.CalculateWithPercentageUseCase;
import com.challenge.challengetenpo.domain.usecase.GetCallHistoryUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public CalculateWithPercentageUseCase calculateWithPercentageUseCase(PercentageGateway percentageGateway) {
        return new CalculateWithPercentageUseCase(percentageGateway);
    }

    @Bean
    public GetCallHistoryUseCase getCallHistoryUseCase(CallHistoryGateway callHistoryGateway) {
        return new GetCallHistoryUseCase(callHistoryGateway);
    }
}
