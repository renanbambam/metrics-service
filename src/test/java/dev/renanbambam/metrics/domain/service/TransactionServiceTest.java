package dev.renanbambam.metrics.domain.service;

import dev.renanbambam.metrics.domain.model.ProcessingStatus;
import dev.renanbambam.metrics.domain.model.Transaction;
import dev.renanbambam.metrics.domain.model.TransactionResult;
import dev.renanbambam.metrics.domain.model.TransactionType;
import dev.renanbambam.metrics.domain.port.outbound.TransactionMetricsPort;
import dev.renanbambam.metrics.domain.port.outbound.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private TransactionMetricsPort metrics;

    private TransactionService service;

    @Test
    void deveSalvarTransacaoERegistrarMetricaDeSucesso() {
        service = new TransactionService(repository, metrics);
        Transaction input = Transaction.createNew("acc-1", new BigDecimal("100.00"), TransactionType.DEBIT);
        Transaction saved = new Transaction("tx-1", "acc-1", new BigDecimal("100.00"), TransactionType.DEBIT, Instant.now());
        when(repository.save(input)).thenReturn(saved);

        TransactionResult result = service.create(input);

        assertThat(result.id()).isEqualTo("tx-1");
        assertThat(result.status()).isEqualTo(ProcessingStatus.COMPLETED);
        verify(metrics).recordSuccess(new BigDecimal("100.00"));
    }

    @Test
    void devePropagarErroDoRepositorioSemEngolir() {
        service = new TransactionService(repository, metrics);
        Transaction input = Transaction.createNew("acc-1", new BigDecimal("50.00"), TransactionType.CREDIT);
        when(repository.save(input)).thenThrow(new IllegalStateException("banco fora do ar"));

        try {
            service.create(input);
            org.junit.jupiter.api.Assertions.fail("esperava exceção");
        } catch (IllegalStateException expected) {
            assertThat(expected.getMessage()).isEqualTo("banco fora do ar");
        }
    }

    @Test
    void deveListarTransacoesPorConta() {
        service = new TransactionService(repository, metrics);
        Transaction tx = new Transaction("tx-1", "acc-1", new BigDecimal("10.00"), TransactionType.TRANSFER, Instant.now());
        when(repository.findByAccount("acc-1")).thenReturn(List.of(tx));

        List<Transaction> result = service.listByAccount("acc-1");

        assertThat(result).containsExactly(tx);
    }

    @Test
    void deveRetornarListaVaziaQuandoContaNaoTemTransacoes() {
        service = new TransactionService(repository, metrics);
        when(repository.findByAccount(any())).thenReturn(List.of());

        List<Transaction> result = service.listByAccount("acc-sem-movimento");

        assertThat(result).isEmpty();
    }
}
