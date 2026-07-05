package dev.renanbambam.metrics.observability;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMetricsTest {

    private SimpleMeterRegistry registry;
    private TransactionMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new TransactionMetrics(registry);
    }

    @Test
    void deveIncrementarContadorDeSucessoERegistrarValor() {
        metrics.recordSuccess(new BigDecimal("150.00"));

        assertThat(registry.get("transactions.total").counter().count()).isEqualTo(1.0);
        assertThat(registry.get("transactions.amount").summary().totalAmount()).isEqualTo(150.00);
    }

    @Test
    void deveIncrementarContadorDeFalha() {
        metrics.recordFailure("IllegalStateException");

        assertThat(registry.get("transactions.failed").counter().count()).isEqualTo(1.0);
    }

    @Test
    void deveRegistrarTempoDeProcessamento() {
        metrics.recordProcessingTime(Duration.ofMillis(250));

        assertThat(registry.get("transactions.processing.time").timer().count()).isEqualTo(1);
    }
}
