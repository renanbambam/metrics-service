package dev.renanbambam.metrics.adapter.outbound.persistence.adapter;

import dev.renanbambam.metrics.adapter.outbound.persistence.repository.TransactionJpaRepository;
import dev.renanbambam.metrics.domain.model.Transaction;
import dev.renanbambam.metrics.domain.port.outbound.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionPersistenceAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionEntityMapper mapper;

    public TransactionPersistenceAdapter(TransactionJpaRepository jpaRepository, TransactionEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = mapper.toNewEntity(transaction);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Transaction> findByAccount(String accountId) {
        return jpaRepository.findByAccountId(accountId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
