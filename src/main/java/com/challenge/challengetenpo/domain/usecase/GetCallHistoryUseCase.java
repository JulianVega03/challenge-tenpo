package com.challenge.challengetenpo.domain.usecase;

import com.challenge.challengetenpo.domain.model.CallHistory;
import com.challenge.challengetenpo.domain.model.PageQuery;
import com.challenge.challengetenpo.domain.model.PageResult;
import com.challenge.challengetenpo.domain.model.gateway.CallHistoryGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetCallHistoryUseCase {

    private final CallHistoryGateway callHistoryGateway;

    public PageResult<CallHistory> execute(PageQuery request) {
        return callHistoryGateway.findAll(request);
    }
}
