package com.tempo.challengetempo.controllers;

import com.tempo.challengetempo.dtos.CallHistoryResponseDto;
import com.tempo.challengetempo.serivces.HistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistoryController.class)
public class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HistoryService historyService;

    private static final String BASE_URL = "/api/history";

    @TestConfiguration
    static class TestConfig {
        @Bean
        public HistoryService historyService() {
            return mock(HistoryService.class);
        }
    }

    @Test
    void getHistory_shouldReturnOkAndPagedHistory() throws Exception {
        CallHistoryResponseDto dto = new CallHistoryResponseDto(
                "/calculator/sum",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                "n1=10&n2=20",
                "33.0",
                null
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<CallHistoryResponseDto> mockPage = new PageImpl<>(
                Collections.singletonList(dto),
                pageable,
                1
        );

        when(historyService.getHistory(any(Pageable.class))).thenReturn(mockPage);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "date,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].endpoint").value(dto.endpoint()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getHistory_shouldReturnOkAndEmptyPage_whenNoHistoryExists() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CallHistoryResponseDto> mockEmptyPage = Page.empty(pageable);

        when(historyService.getHistory(any(Pageable.class))).thenReturn(mockEmptyPage);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

}
