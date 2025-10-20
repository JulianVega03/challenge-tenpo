package com.tempo.challengetempo.serivces.impl;

import com.tempo.challengetempo.config.ExternalServiceProperties;
import com.tempo.challengetempo.exceptions.CacheUnavailableException;
import com.tempo.challengetempo.exceptions.ExternalServiceException;
import com.tempo.challengetempo.serivces.PercentageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PercentageServiceImpl implements PercentageService {

    private final RestClient externalRestClient;
    private final ExternalServiceProperties properties;
    private final CacheManager cacheManager;

    private static final String CACHE_NAME = "percentageCache";
    private static final String CACHE_KEY = "latestPercentage";

    @Override
    public double getPercentage() {
        try {
            ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<>() {};
            Map<String, Object> response = externalRestClient.get()
                    .uri(properties.getPercentagePath())
                    .retrieve()
                    .body(typeRef);

            if (response == null || !response.containsKey("percentage")) {
                throw new ExternalServiceException("Respuesta inválida o incompleta del servicio externo.");
            }

            double percentage = ((Number) response.get("percentage")).doubleValue();

            cacheManager.getCache(CACHE_NAME).put(CACHE_KEY, percentage);
            return percentage;

        } catch (RestClientException | ExternalServiceException ex) {
            Cache cache = cacheManager.getCache(CACHE_NAME);
            Double cachedValue = cache != null ? cache.get(CACHE_KEY, Double.class) : null;

            if (cachedValue != null) {
                return cachedValue;
            }

            throw new CacheUnavailableException("No se pudo obtener el porcentaje y la caché está vacía.");
        }
    }
}
