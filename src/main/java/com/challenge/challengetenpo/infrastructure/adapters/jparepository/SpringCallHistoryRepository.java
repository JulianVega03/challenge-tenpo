package com.challenge.challengetenpo.infrastructure.adapters.jparepository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringCallHistoryRepository extends JpaRepository<CallHistoryEntity, UUID> {
}
