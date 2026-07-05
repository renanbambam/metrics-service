package dev.renanbambam.metrics.observability;

import dev.renanbambam.metrics.domain.port.outbound.TransactionMetricsPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;

@Component
public class TransactionMetrics implements TransactionMetricsPort {

    private final MeterRegistry registry;
    private final Counter totalTransactions;
    private final Timer processingTime;
    private final DistributionSummary amountSummary;

    public TransactionMetrics(MeterRegistry registry) {
        this.registry = registry;

        this.totalTransactions = Counter.builder("transactions.total")
                .description("total de transações processadas com sucesso")
                .register(registry);

        this.processingTime = Timer.builder("transactions.processing.time")
                .description("tempo de processamento por transação")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);

        this.amountSummary = DistributionSummary.builder("transactions.amount")
                .description("distribuição dos valores de transação")
                .baseUnit("BRL")
                .register(registry);
    }

    @Override
    public void recordSuccess(BigDecimal amount) {
        totalTransactions.increment();
        amountSummary.record(amount.doubleValue());
    }

    @Override
    public void recordFailure(String reason) {
        Counter.builder("transactions.failed")
                .description("transações que falharam, por motivo")
                .tag("reason", reason)
                .register(registry)
                .increment();
    }

    void recordProcessingTime(Duration duration) {
        processingTime.record(duration);
    }
}
