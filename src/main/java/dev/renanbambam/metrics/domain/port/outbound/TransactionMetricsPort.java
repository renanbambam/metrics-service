package dev.renanbambam.metrics.domain.port.outbound;

import java.math.BigDecimal;

public interface TransactionMetricsPort {

    void recordSuccess(BigDecimal amount);

    void recordFailure(String reason);
}
