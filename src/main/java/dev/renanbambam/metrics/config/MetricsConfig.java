package dev.renanbambam.metrics.config;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    // histograma completo (não só percentis pré-calculados) pra dar pra montar heatmap no Grafana
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> transactionsHistogramConfig() {
        return registry -> registry.config().meterFilter(new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if (!id.getName().startsWith("transactions.")) {
                    return config;
                }
                return DistributionStatisticConfig.builder()
                        .percentilesHistogram(true)
                        .build()
                        .merge(config);
            }
        });
    }
}
