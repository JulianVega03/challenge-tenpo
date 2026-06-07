package com.challenge.challengetenpo.domain.usecase;

import com.challenge.challengetenpo.domain.model.Calculation;
import com.challenge.challengetenpo.domain.model.exception.ExternalServiceException;
import com.challenge.challengetenpo.domain.model.gateway.PercentageGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateWithPercentageUseCaseTest {

    @Mock
    private PercentageGateway percentageGateway;

    @InjectMocks
    private CalculateWithPercentageUseCase useCase;

    @Test
    void shouldApplyPercentageToSum() {
        when(percentageGateway.getPercentage()).thenReturn(new BigDecimal("10"));

        Calculation result = useCase.calculate(new BigDecimal("5"), new BigDecimal("5"));

        assertThat(result.result()).isEqualByComparingTo("11.00");
        assertThat(result.percentage()).isEqualByComparingTo("10");
        assertThat(result.num1()).isEqualByComparingTo("5");
        assertThat(result.num2()).isEqualByComparingTo("5");
    }

    @Test
    void shouldReturnExactSumWhenPercentageIsZero() {
        when(percentageGateway.getPercentage()).thenReturn(BigDecimal.ZERO);

        Calculation result = useCase.calculate(new BigDecimal("5"), new BigDecimal("5"));

        assertThat(result.result()).isEqualByComparingTo("10.00");
    }

    @Test
    void shouldHandleNegativeNumbers() {
        when(percentageGateway.getPercentage()).thenReturn(new BigDecimal("10"));

        Calculation result = useCase.calculate(new BigDecimal("-5"), new BigDecimal("-5"));

        assertThat(result.result()).isEqualByComparingTo("-11.00");
    }

    @Test
    void shouldHandleDecimalInputs() {
        when(percentageGateway.getPercentage()).thenReturn(new BigDecimal("10"));

        Calculation result = useCase.calculate(new BigDecimal("2.5"), new BigDecimal("2.5"));

        assertThat(result.result()).isEqualByComparingTo("5.50");
    }

    @Test
    void shouldPropagateExternalServiceException() {
        when(percentageGateway.getPercentage())
                .thenThrow(new ExternalServiceException("service down", null));

        assertThatThrownBy(() -> useCase.calculate(BigDecimal.ONE, BigDecimal.ONE))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessage("service down");
    }
}
