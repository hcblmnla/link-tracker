package backend.academy.bot.config.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerConfig {

    public static void createCounterAndIncrement(
            final String name, final PrometheusMeterRegistry prometheusMeterRegistry) {
        prometheusMeterRegistry.counter(name).increment();
    }

    @Bean
    public Counter userMessagesCounter(final PrometheusMeterRegistry prometheusMeterRegistry) {
        return prometheusMeterRegistry.counter("user_messages");
    }
}
