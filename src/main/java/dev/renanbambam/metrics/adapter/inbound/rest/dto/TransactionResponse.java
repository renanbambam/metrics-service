package dev.renanbambam.metrics.adapter.inbound.rest.dto;

import dev.renanbambam.metrics.domain.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        String id,
        String accountId,
        BigDecimal amount,
        TransactionType type,
        String status,
        Instant processedAt
) {
}
