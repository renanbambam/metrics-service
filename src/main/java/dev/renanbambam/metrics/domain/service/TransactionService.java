package dev.renanbambam.metrics.domain.service;

import dev.renanbambam.metrics.domain.model.ProcessingStatus;
import dev.renanbambam.metrics.domain.model.Transaction;
import dev.renanbambam.metrics.domain.model.TransactionResult;
import dev.renanbambam.metrics.domain.port.inbound.CreateTransactionPort;
import dev.renanbambam.metrics.domain.port.inbound.ListTransactionsPort;
import dev.renanbambam.metrics.domain.port.outbound.TransactionMetricsPort;
import dev.renanbambam.metrics.domain.port.outbound.TransactionRepository;

import java.time.Instant;
import java.util.List;

public class TransactionService implements CreateTransactionPort, ListTransactionsPort {

    private final TransactionRepository repository;
    private final TransactionMetricsPort metrics;

    public TransactionService(TransactionRepository repository, TransactionMetricsPort metrics) {
        this.repository = repository;
        this.metrics = metrics;
    }

    @Override
    public TransactionResult create(Transaction transaction) {
        Transaction saved = repository.save(transaction);
        metrics.recordSuccess(saved.amount());
        return new TransactionResult(saved.id(), ProcessingStatus.COMPLETED, Instant.now());
    }

    @Override
    public List<Transaction> listByAccount(String accountId) {
        return repository.findByAccount(accountId);
    }
}
