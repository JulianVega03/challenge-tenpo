package com.tempo.challengetempo.filters;

import com.tempo.challengetempo.entities.CallHistory;
import com.tempo.challengetempo.serivces.HistoryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class HistoryLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_RESPONSE_LENGTH = 2000;
    private final HistoryService historyService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String endpoint = request.getMethod() + " " + request.getRequestURI();
        Throwable error = null;
        String responseBody = null;
        String errorMessage = null;

        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } catch (Throwable t) {
            error = t;
            errorMessage = "Excepción no manejada: " + t.getMessage();
            throw t;
        } finally {
            int status = responseWrapper.getStatus();
            boolean success = (error == null && status >= 200 && status < 400);

            String requestDetails = extractRequestDetails(requestWrapper);

            if (success) {
                responseBody = extractResponseBody(responseWrapper);
            } else {
                if (errorMessage == null) {
                    errorMessage = "Fallo HTTP - Status: " + status;
                }
            }

            CallHistory callHistory = CallHistory.builder()
                    .date(LocalDateTime.now())
                    .endpoint(endpoint)
                    .parameters(requestDetails)
                    .response(responseBody)
                    .error(errorMessage)
                    .build();

            historyService.registerHistoryAsync(callHistory);

            responseWrapper.copyBodyToResponse();
        }
    }

    private String extractRequestDetails(ContentCachingRequestWrapper requestWrapper) {
        String queryString = requestWrapper.getQueryString();
        return (queryString != null && !queryString.isEmpty()) ? queryString : "";
    }

    private String extractResponseBody(ContentCachingResponseWrapper responseWrapper) {
        byte[] content = responseWrapper.getContentAsByteArray();
        if (content.length > 0) {
            int length = Math.min(content.length, MAX_RESPONSE_LENGTH);
            return new String(content, 0, length, StandardCharsets.UTF_8);
        }
        return "Status: " + responseWrapper.getStatus() + " (No Content)";
    }

}