package com.challenge.challengetenpo.infrastructure.entrypoints.apirest.mock;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/mock")
@ConditionalOnProperty(name = "external.percentage.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockPercentageController {

    @GetMapping("/percentage")
    public Map<String, BigDecimal> getPercentage() {
        return Map.of("percentage", new BigDecimal("10.0"));
    }
}
