package dev.renanbambam.metrics.domain.port.inbound;

import dev.renanbambam.metrics.domain.model.Transaction;
import dev.renanbambam.metrics.domain.model.TransactionResult;

public interface CreateTransactionPort {

    TransactionResult create(Transaction transaction);
}
