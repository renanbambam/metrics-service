package dev.renanbambam.metrics.adapter.inbound.rest.dto;

import dev.renanbambam.metrics.domain.model.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotBlank String accountId,
        @NotNull @Positive BigDecimal amount,
        @NotNull TransactionType type
) {
}
