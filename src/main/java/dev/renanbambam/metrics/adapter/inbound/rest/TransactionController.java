package dev.renanbambam.metrics.adapter.inbound.rest;

import dev.renanbambam.metrics.adapter.inbound.rest.dto.TransactionRequest;
import dev.renanbambam.metrics.adapter.inbound.rest.dto.TransactionResponse;
import dev.renanbambam.metrics.adapter.inbound.rest.mapper.TransactionMapper;
import dev.renanbambam.metrics.domain.model.Transaction;
import dev.renanbambam.metrics.domain.model.TransactionResult;
import dev.renanbambam.metrics.domain.port.inbound.CreateTransactionPort;
import dev.renanbambam.metrics.domain.port.inbound.ListTransactionsPort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final CreateTransactionPort createTransactionPort;
    private final ListTransactionsPort listTransactionsPort;
    private final TransactionMapper mapper;

    public TransactionController(
            CreateTransactionPort createTransactionPort,
            ListTransactionsPort listTransactionsPort,
            TransactionMapper mapper
    ) {
        this.createTransactionPort = createTransactionPort;
        this.listTransactionsPort = listTransactionsPort;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = mapper.toDomain(request);
        TransactionResult result = createTransactionPort.create(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(result, transaction));
    }

    @GetMapping
    public List<TransactionResponse> listByAccount(@RequestParam String accountId) {
        return listTransactionsPort.listByAccount(accountId).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
