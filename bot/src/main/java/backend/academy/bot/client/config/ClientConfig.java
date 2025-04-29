package backend.academy.bot.client.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import java.time.Duration;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@ConfigurationProperties(prefix = "client")
public record ClientConfig(Duration timeout, RetryConfig retry, CBConfig cb) {

    @Bean
    public CircuitBreaker configuredCB() {
        final CircuitBreakerConfig cbConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(cb.slidingWindowSize())
                .minimumNumberOfCalls(cb.minimumRequiredCalls())
                .failureRateThreshold(cb.failureRateThreshold())
                .permittedNumberOfCallsInHalfOpenState(cb.permittedCallsInHalfOpenState())
                .waitDurationInOpenState(cb.waitDurationInOpenState())
                .build();
        return CircuitBreaker.of(cb.name(), cbConfig);
    }

    public record RetryConfig(int maxAttempts, Duration minBackoff, Set<Integer> retryableStatuses) {}

    public record CBConfig(
            int slidingWindowSize,
            int minimumRequiredCalls,
            float failureRateThreshold,
            int permittedCallsInHalfOpenState,
            Duration waitDurationInOpenState,
            String name) {}
}
