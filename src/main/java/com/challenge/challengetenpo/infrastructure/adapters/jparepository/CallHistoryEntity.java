package com.challenge.challengetenpo.infrastructure.adapters.jparepository;

import com.challenge.challengetenpo.domain.model.CallHistory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "call_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CallHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 500)
    private String endpoint;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "http_status", nullable = false)
    private int httpStatus;

    public CallHistory toDomain() {
        return new CallHistory(id, timestamp, endpoint, method, parameters, response, errorMessage, httpStatus);
    }

    public static CallHistoryEntity fromDomain(CallHistory history) {
        return CallHistoryEntity.builder()
                .id(history.id())
                .timestamp(history.timestamp())
                .endpoint(history.endpoint())
                .method(history.method())
                .parameters(history.parameters())
                .response(history.response())
                .errorMessage(history.errorMessage())
                .httpStatus(history.httpStatus())
                .build();
    }
}
