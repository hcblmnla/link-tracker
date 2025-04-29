package backend.academy.bot.client.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@ConfigurationProperties(prefix = "rate-limit")
public record RateLimitConfig(int forPeriod, Duration refreshPeriod, Duration timeout, String rateLimiter) {

    @Bean
    @Primary
    public RateLimiterRegistry rateLimitRegistry() {
        return RateLimiterRegistry.of(RateLimiterConfig.custom()
                .limitForPeriod(forPeriod)
                .limitRefreshPeriod(refreshPeriod)
                .timeoutDuration(timeout)
                .build());
    }

    @Bean
    public RateLimiter ipRateLimit(final RateLimiterRegistry registry) {
        return registry.rateLimiter(rateLimiter);
    }
}
