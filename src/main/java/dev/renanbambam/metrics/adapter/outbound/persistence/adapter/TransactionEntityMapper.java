package dev.renanbambam.metrics.adapter.outbound.persistence.adapter;

import dev.renanbambam.metrics.adapter.outbound.persistence.entity.TransactionEntity;
import dev.renanbambam.metrics.domain.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TransactionEntityMapper {

    public TransactionEntity toNewEntity(Transaction transaction) {
        return new TransactionEntity(
                transaction.accountId(),
                transaction.amount(),
                transaction.type(),
                Instant.now()
        );
    }

    public Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getAccountId(),
                entity.getAmount(),
                entity.getType(),
                entity.getCreatedAt()
        );
    }
}
