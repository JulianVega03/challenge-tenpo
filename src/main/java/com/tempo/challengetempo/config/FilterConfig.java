// Archivo: FilterConfig.java (Mejorado)

package com.tempo.challengetempo.config;

import com.tempo.challengetempo.filters.HistoryLoggingFilter;
import com.tempo.challengetempo.serivces.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

    private static final String HISTORY_FILTER_URL_PATTERN = "/api/calculator/*";

    @Bean
    public HistoryLoggingFilter historyLoggingFilter(HistoryService historyService) {
        return new HistoryLoggingFilter(historyService);
    }

    @Bean
    public FilterRegistrationBean<HistoryLoggingFilter> loggingFilter(HistoryLoggingFilter historyLoggingFilter) {
        FilterRegistrationBean<HistoryLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(historyLoggingFilter);
        registrationBean.addUrlPatterns(HISTORY_FILTER_URL_PATTERN);
        registrationBean.setOrder(1);

        return registrationBean;
    }
}