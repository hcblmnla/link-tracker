package backend.academy.bot.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.bot.config.micrometer.MicrometerConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MetricsLoaderTest {

    private PrometheusMeterRegistry prometheusMeterRegistry;

    @BeforeEach
    void setUp() {
        prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        Metrics.globalRegistry.add(prometheusMeterRegistry);
    }

    @AfterEach
    void tearDown() {
        prometheusMeterRegistry.clear();
        Metrics.globalRegistry.clear();
    }

    @Test
    void metrics_shouldLoads() {
        // given-when
        MicrometerConfig.createCounterAndIncrement("test", prometheusMeterRegistry);

        // then
        final Counter counter = prometheusMeterRegistry.find("test").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1);
    }
}
