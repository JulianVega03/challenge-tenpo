package com.challenge.challengetenpo.infrastructure.entrypoints.apirest.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter();
    }

    @Test
    void shouldAllowRequestsWithinRateLimit() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/calculate");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(response.getStatus()).isNotEqualTo(429);
    }

    @Test
    void shouldReturn429WhenRateLimitExceeded() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/calculate");
        FilterChain chain = mock(FilterChain.class);

        for (int i = 0; i < 3; i++) {
            filter.doFilter(request, new MockHttpServletResponse(), chain);
        }

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilter(request, blockedResponse, chain);

        assertThat(blockedResponse.getStatus()).isEqualTo(429);
        assertThat(blockedResponse.getContentType()).isEqualTo("application/json");
    }

    @Test
    void shouldPassThroughNonApiPaths() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/mock/percentage");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        for (int i = 0; i < 10; i++) {
            filter.doFilter(request, response, chain);
        }

        verify(chain, times(10)).doFilter(request, response);
        assertThat(response.getStatus()).isNotEqualTo(429);
    }
}
