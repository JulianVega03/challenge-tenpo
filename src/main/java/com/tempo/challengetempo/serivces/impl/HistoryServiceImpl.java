package com.tempo.challengetempo.serivces.impl;

import com.tempo.challengetempo.dtos.CallHistoryResponseDto;
import com.tempo.challengetempo.entities.CallHistory;
import com.tempo.challengetempo.mappers.CallHistoryMapper;
import com.tempo.challengetempo.repositories.CallHistoryRepository;
import com.tempo.challengetempo.serivces.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final CallHistoryRepository callHistoryRepository;
    private final CallHistoryMapper mapper;

    @Override
    public Page<CallHistoryResponseDto> getHistory(Pageable pageable) {
        return callHistoryRepository.findAllByOrderByDateDesc(pageable)
                .map(mapper::toDto);
    }

    @Override
    @Async
    public void registerHistoryAsync(CallHistory callHistory) {
        callHistoryRepository.save(callHistory);
    }

}
