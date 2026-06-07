package com.challenge.challengetenpo.domain.model.gateway;

import com.challenge.challengetenpo.domain.model.CallHistory;
import com.challenge.challengetenpo.domain.model.PageQuery;
import com.challenge.challengetenpo.domain.model.PageResult;

public interface CallHistoryGateway {
    void save(CallHistory history);
    PageResult<CallHistory> findAll(PageQuery request);
}
