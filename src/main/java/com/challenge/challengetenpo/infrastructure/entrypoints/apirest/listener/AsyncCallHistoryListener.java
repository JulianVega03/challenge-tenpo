package com.challenge.challengetenpo.infrastructure.entrypoints.apirest.listener;

import com.challenge.challengetenpo.domain.model.CallHistory;
import com.challenge.challengetenpo.domain.model.gateway.CallHistoryGateway;
import com.challenge.challengetenpo.infrastructure.entrypoints.apirest.event.CallHistoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncCallHistoryListener {

    private final CallHistoryGateway callHistoryGateway;

    @EventListener
    @Async
    public void onCallHistory(CallHistoryEvent event) {
        try {
            callHistoryGateway.save(new CallHistory(
                    null,
                    event.timestamp(),
                    event.endpoint(),
                    event.method(),
                    event.parameters(),
                    event.response(),
                    event.errorMessage(),
                    event.httpStatus()
            ));
        } catch (Exception ex) {
            log.error("Failed to save call history for {}: {}", event.endpoint(), ex.getMessage());
        }
    }
}
