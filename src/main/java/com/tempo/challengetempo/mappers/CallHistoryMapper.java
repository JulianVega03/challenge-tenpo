package com.tempo.challengetempo.mappers;

import com.tempo.challengetempo.dtos.CallHistoryResponseDto;
import com.tempo.challengetempo.entities.CallHistory;
import org.springframework.stereotype.Component;

@Component
public class CallHistoryMapper {

    public CallHistoryResponseDto toDto(CallHistory entity) {
        if (entity == null) return null;
        return new CallHistoryResponseDto(
                entity.getEndpoint(),
                entity.getDate(),
                entity.getParameters(),
                entity.getResponse(),
                entity.getError()
        );
    }

    public CallHistory toEntity(CallHistoryResponseDto dto) {
        if (dto == null) return null;
        return CallHistory.builder()
                .endpoint(dto.endpoint())
                .date(dto.date())
                .parameters(dto.parameters())
                .response(dto.response())
                .error(dto.error())
                .build();
    }

}
