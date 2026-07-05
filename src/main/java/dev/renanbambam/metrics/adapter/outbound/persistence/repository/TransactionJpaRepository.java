package dev.renanbambam.metrics.adapter.outbound.persistence.repository;

import dev.renanbambam.metrics.adapter.outbound.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findByAccountId(String accountId);
}
