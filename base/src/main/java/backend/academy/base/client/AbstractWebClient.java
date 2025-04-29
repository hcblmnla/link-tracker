package backend.academy.base.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

public abstract class AbstractWebClient {

    public static final List<MediaType> ACCEPTABLE_MEDIA_TYPES = List.of(MediaType.APPLICATION_JSON);
    protected final WebClient webClient;

    protected AbstractWebClient(
            final String baseUrl,
            final Duration timeout,
            final int maxRetryAttempts,
            final Duration minBackoff,
            final Set<Integer> retryableStatuses,
            final CircuitBreaker circuitBreaker,
            final RateLimiter rateLimiter) {
        final ExchangeFilterFunction retryFilter = (clientRequest, next) -> next.exchange(clientRequest)
                .flatMap(response -> {
                    if (retryableStatuses.contains(response.statusCode().value())) {
                        return Mono.error(new RetryableException());
                    }
                    return Mono.just(response);
                })
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .retryWhen(Retry.backoff(maxRetryAttempts, minBackoff)
                        .filter(e -> e instanceof RetryableException)
                        .onRetryExhaustedThrow(this::createRetryException));

        final HttpClient httpClient = HttpClient.create()
                .responseTimeout(timeout)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeout.toMillis());

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(retryFilter)
                .defaultHeaders(hs -> hs.setAccept(ACCEPTABLE_MEDIA_TYPES))
                .build();
    }

    private RetryExhaustedException createRetryException(final RetryBackoffSpec spec, final Retry.RetrySignal signal) {
        return new RetryExhaustedException(spec.toString(), signal.totalRetries());
    }

    @NoArgsConstructor
    private static class RetryableException extends RuntimeException {}
}
