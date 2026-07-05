package dev.renanbambam.metrics.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record Transaction(String id, String accountId, BigDecimal amount, TransactionType type, Instant createdAt) {

    public Transaction {
        Objects.requireNonNull(accountId, "accountId não pode ser nulo");
        Objects.requireNonNull(amount, "amount não pode ser nulo");
        Objects.requireNonNull(type, "type não pode ser nulo");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount precisa ser maior que zero");
        }
    }

    public static Transaction createNew(String accountId, BigDecimal amount, TransactionType type) {
        return new Transaction(null, accountId, amount, type, null);
    }
}
