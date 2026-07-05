package dev.renanbambam.metrics.domain.model;

import java.time.Instant;

public record TransactionResult(String id, ProcessingStatus status, Instant processedAt) {
}
