package com.challenge.challengetenpo.infrastructure.entrypoints.apirest.filter;

import com.challenge.challengetenpo.infrastructure.entrypoints.apirest.event.CallHistoryEvent;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Order(2)
@RequiredArgsConstructor
public class HistoryLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_SIZE = 2048;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator") || uri.startsWith("/h2") || uri.startsWith("/mock")
                || uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs") || uri.startsWith("/webjars");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        LocalDateTime timestamp = LocalDateTime.now();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_BODY_SIZE);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            publishHistoryEvent(wrappedRequest, wrappedResponse, timestamp);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void publishHistoryEvent(ContentCachingRequestWrapper request,
                                     ContentCachingResponseWrapper response,
                                     LocalDateTime timestamp) {
        String params = request.getQueryString();
        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
        if (responseBody.length() > MAX_BODY_SIZE) {
            responseBody = responseBody.substring(0, MAX_BODY_SIZE) + "...[truncated]";
        }

        int status = response.getStatus();
        boolean isError = status >= 400;

        eventPublisher.publishEvent(new CallHistoryEvent(
                request.getRequestURI(),
                request.getMethod(),
                params,
                isError ? null : responseBody,
                isError ? responseBody : null,
                status,
                timestamp
        ));
    }
}
