package dev.renanbambam.metrics.domain.port.outbound;

import dev.renanbambam.metrics.domain.model.Transaction;

import java.util.List;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findByAccount(String accountId);
}
