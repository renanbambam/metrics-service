package dev.renanbambam.metrics.adapter.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.renanbambam.metrics.adapter.inbound.rest.GlobalExceptionHandler;
import dev.renanbambam.metrics.adapter.inbound.rest.TransactionController;
import dev.renanbambam.metrics.adapter.inbound.rest.mapper.TransactionMapper;
import dev.renanbambam.metrics.domain.model.ProcessingStatus;
import dev.renanbambam.metrics.domain.model.Transaction;
import dev.renanbambam.metrics.domain.model.TransactionResult;
import dev.renanbambam.metrics.domain.model.TransactionType;
import dev.renanbambam.metrics.domain.port.inbound.CreateTransactionPort;
import dev.renanbambam.metrics.domain.port.inbound.ListTransactionsPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import({TransactionMapper.class, GlobalExceptionHandler.class})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateTransactionPort createTransactionPort;

    @MockBean
    private ListTransactionsPort listTransactionsPort;

    @Test
    void deveCriarTransacaoERetornar201() throws Exception {
        TransactionResult result = new TransactionResult("tx-1", ProcessingStatus.COMPLETED, Instant.now());
        when(createTransactionPort.create(any())).thenReturn(result);

        String body = """
                {"accountId":"acc-1","amount":100.00,"type":"DEBIT"}
                """;

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("tx-1"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deveRejeitarTransacaoComValorNegativo() throws Exception {
        String body = """
                {"accountId":"acc-1","amount":-10.00,"type":"DEBIT"}
                """;

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveListarTransacoesDaConta() throws Exception {
        Transaction tx = new Transaction("tx-1", "acc-1", new BigDecimal("25.00"), TransactionType.CREDIT, Instant.now());
        when(listTransactionsPort.listByAccount(eq("acc-1"))).thenReturn(List.of(tx));

        mockMvc.perform(get("/api/v1/transactions").param("accountId", "acc-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("tx-1"))
                .andExpect(jsonPath("$[0].accountId").value("acc-1"));
    }
}
