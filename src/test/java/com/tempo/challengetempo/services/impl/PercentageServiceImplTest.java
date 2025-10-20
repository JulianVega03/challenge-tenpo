package com.tempo.challengetempo.services.impl;

import com.tempo.challengetempo.config.ExternalServiceProperties;
import com.tempo.challengetempo.exceptions.CacheUnavailableException;
import com.tempo.challengetempo.exceptions.ExternalServiceException;
import com.tempo.challengetempo.serivces.impl.PercentageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersUriSpec;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import org.springframework.web.client.RestClientException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PercentageServiceImplTest {

    @InjectMocks
    private PercentageServiceImpl percentageService;

    @Mock
    private RestClient externalRestClient;

    @Mock
    private ExternalServiceProperties properties;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private RequestHeadersSpec requestHeadersSpec;
    @Mock
    private ResponseSpec responseSpec;
    @Mock
    private Cache cache;

    private static final String CACHE_NAME = "percentageCache";
    private static final String CACHE_KEY = "latestPercentage";
    private static final String PERCENTAGE_PATH = "/api/percentage";
    private static final double TEST_PERCENTAGE = 15.5;

    @BeforeEach
    void setUp() {
        when(externalRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(PERCENTAGE_PATH)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(properties.getPercentagePath()).thenReturn(PERCENTAGE_PATH);
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
    }

    @Test
    void getPercentage_shouldFetchAndCacheSuccessfully() {
        Map<String, Object> successfulResponse = Map.of("percentage", TEST_PERCENTAGE);

        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(successfulResponse);

        double result = percentageService.getPercentage();

        assertEquals(TEST_PERCENTAGE, result);

        verify(cache, times(1)).put(CACHE_KEY, TEST_PERCENTAGE);
        verify(cache, never()).get(eq(CACHE_KEY), eq(Double.class));
    }

    @Test
    void getPercentage_shouldThrowCacheUnavailableException_whenResponseIsInvalidAndCacheIsEmpty() {
        Map<String, Object> invalidResponse = Map.of("prueba", "valor");
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(invalidResponse);

        when(cache.get(eq(CACHE_KEY), eq(Double.class))).thenReturn(null);

        assertThrows(CacheUnavailableException.class, () -> percentageService.getPercentage());

        verify(cache, times(1)).get(eq(CACHE_KEY), eq(Double.class));
        verify(cache, never()).put(any(), any());
    }

    @Test
    void getPercentage_shouldReturnCachedValue_onRestClientError() {
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenThrow(new RestClientException("Error de conexión"));

        double cachedPercentage = 5.0;
        when(cache.get(eq(CACHE_KEY), eq(Double.class))).thenReturn(cachedPercentage);

        double result = percentageService.getPercentage();

        assertEquals(cachedPercentage, result);
        verify(cache, times(1)).get(eq(CACHE_KEY), eq(Double.class));
        verify(cache, never()).put(any(), any());
    }

    @Test
    void getPercentage_shouldThrowCacheUnavailableException_onExternalErrorAndEmptyCache() {
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenThrow(new ExternalServiceException("Error del servicio"));

        when(cache.get(eq(CACHE_KEY), eq(Double.class))).thenReturn(null);

        assertThrows(CacheUnavailableException.class, () -> percentageService.getPercentage());

        verify(cache, times(1)).get(eq(CACHE_KEY), eq(Double.class));
        verify(cache, never()).put(any(), any());
    }
}
