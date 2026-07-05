package dev.renanbambam.metrics.adapter.inbound.rest.mapper;

import dev.renanbambam.metrics.adapter.inbound.rest.dto.TransactionRequest;
import dev.renanbambam.metrics.adapter.inbound.rest.dto.TransactionResponse;
import dev.renanbambam.metrics.domain.model.ProcessingStatus;
import dev.renanbambam.metrics.domain.model.Transaction;
import dev.renanbambam.metrics.domain.model.TransactionResult;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toDomain(TransactionRequest request) {
        return Transaction.createNew(request.accountId(), request.amount(), request.type());
    }

    public TransactionResponse toResponse(TransactionResult result, Transaction transaction) {
        return new TransactionResponse(
                result.id(),
                transaction.accountId(),
                transaction.amount(),
                transaction.type(),
                result.status().name(),
                result.processedAt()
        );
    }

    public TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.id(),
                transaction.accountId(),
                transaction.amount(),
                transaction.type(),
                ProcessingStatus.COMPLETED.name(),
                transaction.createdAt()
        );
    }
}
