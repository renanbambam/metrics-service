package dev.renanbambam.metrics.domain.port.inbound;

import dev.renanbambam.metrics.domain.model.Transaction;

import java.util.List;

public interface ListTransactionsPort {

    List<Transaction> listByAccount(String accountId);
}
