package com.challenge.challengetenpo.infrastructure.adapters.jparepository;

import com.challenge.challengetenpo.domain.model.CallHistory;
import com.challenge.challengetenpo.domain.model.PageQuery;
import com.challenge.challengetenpo.domain.model.PageResult;
import com.challenge.challengetenpo.domain.model.gateway.CallHistoryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JpaCallHistoryGateway implements CallHistoryGateway {

    private final SpringCallHistoryRepository repository;

    @Override
    public void save(CallHistory history) {
        repository.save(CallHistoryEntity.fromDomain(history));
    }

    @Override
    public PageResult<CallHistory> findAll(PageQuery request) {
        PageRequest pageable = PageRequest.of(
                request.page(),
                request.size(),
                Sort.by(Sort.Direction.DESC, "timestamp")
        );
        Page<CallHistoryEntity> page = repository.findAll(pageable);
        return new PageResult<>(
                page.getContent().stream().map(CallHistoryEntity::toDomain).toList(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber()
        );
    }
}
