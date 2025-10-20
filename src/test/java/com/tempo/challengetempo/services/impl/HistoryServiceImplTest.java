package com.tempo.challengetempo.services.impl;

import com.tempo.challengetempo.dtos.CallHistoryResponseDto;
import com.tempo.challengetempo.entities.CallHistory;
import com.tempo.challengetempo.mappers.CallHistoryMapper;
import com.tempo.challengetempo.repositories.CallHistoryRepository;
import com.tempo.challengetempo.serivces.impl.HistoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistoryServiceImplTest {

    @InjectMocks
    private HistoryServiceImpl historyService;

    @Mock
    private CallHistoryRepository callHistoryRepository;

    @Mock
    private CallHistoryMapper mapper;

    private final LocalDateTime TEST_DATE = LocalDateTime.of(2025, 10, 20, 10, 0, 0);

    @Test
    void getHistory_shouldReturnPagedHistoryWithCorrectDtoStructure() {
        Pageable pageable = PageRequest.of(0, 10);

        CallHistory entity = CallHistory.builder()
                .id(1L)
                .endpoint("/calculator/sum")
                .date(TEST_DATE)
                .parameters("number1=100&number2=50")
                .response("165.0")
                .error(null)
                .build();

        CallHistoryResponseDto expectedDto = new CallHistoryResponseDto(
                entity.getEndpoint(),
                entity.getDate(),
                entity.getParameters(),
                entity.getResponse(),
                entity.getError()
        );

        List<CallHistory> entityList = Collections.singletonList(entity);
        Page<CallHistory> entityPage = new PageImpl<>(entityList, pageable, entityList.size());

        when(callHistoryRepository.findAllByOrderByDateDesc(pageable)).thenReturn(entityPage);
        when(mapper.toDto(any(CallHistory.class))).thenReturn(expectedDto);

        Page<CallHistoryResponseDto> resultPage = historyService.getHistory(pageable);

        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());

        CallHistoryResponseDto actualDto = resultPage.getContent().getFirst();
        assertEquals(expectedDto.endpoint(), actualDto.endpoint());
        assertEquals(expectedDto.response(), actualDto.response());

        verify(callHistoryRepository, times(1)).findAllByOrderByDateDesc(pageable);
        verify(mapper, times(1)).toDto(any(CallHistory.class));
    }

    @Test
    void registerHistoryAsync_shouldCallSaveOnRepository() {
        CallHistory historyToSave = CallHistory.builder()
                .endpoint("/calculator/sum")
                .date(LocalDateTime.now())
                .parameters("20 + 20")
                .response("44.0")
                .error(null)
                .build();

        historyService.registerHistoryAsync(historyToSave);

        verify(callHistoryRepository, times(1)).save(any(CallHistory.class));
    }

}
