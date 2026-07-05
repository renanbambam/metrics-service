package dev.renanbambam.metrics.config;

import dev.renanbambam.metrics.domain.port.outbound.TransactionMetricsPort;
import dev.renanbambam.metrics.domain.port.outbound.TransactionRepository;
import dev.renanbambam.metrics.domain.service.TransactionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public TransactionService transactionService(TransactionRepository repository, TransactionMetricsPort metrics) {
        return new TransactionService(repository, metrics);
    }
}
