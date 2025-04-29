package backend.academy.scrapper.client.bot;

import backend.academy.scrapper.client.config.ClientConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import java.time.Duration;
import java.util.Set;

public interface ClientTestConfig {

    ClientConfig CLIENT_CONFIG = new ClientConfig(
            Duration.ofSeconds(2),
            new ClientConfig.RetryConfig(1, Duration.ofSeconds(2), Set.of(500, 502, 503, 504)),
            new ClientConfig.CBConfig(1, 1, 100, 1, Duration.ofSeconds(1), "test-cb"));

    RateLimiter TEST_RATE_LIMITER = RateLimiter.of(
            "test-rate-limiter",
            RateLimiterConfig.custom()
                    .limitForPeriod(100)
                    .limitRefreshPeriod(Duration.ofSeconds(1))
                    .timeoutDuration(Duration.ZERO)
                    .build());

    RateLimiter DDOS_RATE_LIMITER = RateLimiter.of(
            "test-rate-limiter",
            RateLimiterConfig.custom()
                    .limitForPeriod(10)
                    .limitRefreshPeriod(Duration.ofSeconds(1))
                    .timeoutDuration(Duration.ofMillis(50))
                    .build());
}
