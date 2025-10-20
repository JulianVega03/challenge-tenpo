package com.tempo.challengetempo.serivces;

import com.tempo.challengetempo.dtos.CallHistoryResponseDto;
import com.tempo.challengetempo.entities.CallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryService {

    Page<CallHistoryResponseDto> getHistory(Pageable pageable);

    void registerHistoryAsync(CallHistory callHistory);

}
