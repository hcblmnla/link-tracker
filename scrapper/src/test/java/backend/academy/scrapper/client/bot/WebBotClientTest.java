package backend.academy.scrapper.client.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.base.schema.ApiErrorException;
import backend.academy.scrapper.client.AbstractWireMockTest;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class WebBotClientTest extends AbstractWireMockTest implements ClientTestConfig {

    private BotClient botClient;

    @BeforeEach
    public void setUp() {
        botClient = new WebBotClient(server.baseUrl(), CLIENT_CONFIG, TEST_RATE_LIMITER);
    }

    @Test
    public void sendUpdate__shouldReturnVoid_whenResponseIs200() {
        // given
        server.stubFor(post("/updates").willReturn(aResponse().withStatus(200)));
        // when
        final Mono<Void> result = botClient.sendUpdate(LINK_UPDATE);
        // then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    public void sendUpdate__shouldThrowException_whenResponseIs400() {
        // given
        server.stubFor(post("/updates")
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"what\"}")));
        // when
        final Mono<Void> result = botClient.sendUpdate(LINK_UPDATE);
        // then
        StepVerifier.create(result).expectError(ApiErrorException.class).verify();
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 503, 504})
    public void sendUpdate__shouldRetry_whenServerReturnsRetryableError(final int code) {
        // given
        server.stubFor(post("/updates")
                .inScenario("Retry Scenario")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(code))
                .willSetStateTo("Retry Attempted"));

        server.stubFor(post("/updates")
                .inScenario("Retry Scenario")
                .whenScenarioStateIs("Retry Attempted")
                .willReturn(aResponse().withStatus(200)));

        // when
        final Mono<Void> result = botClient.sendUpdate(LINK_UPDATE);

        // then
        StepVerifier.create(result).verifyComplete();
        server.verify(2, postRequestedFor(urlEqualTo("/updates")));
    }

    @Test
    public void sendUpdate__shouldNotRetry_whenServerReturnsNonRetryableError() {
        // given
        server.stubFor(post("/updates")
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\":\"bad request\"}")));

        // when
        final Mono<Void> result = botClient.sendUpdate(LINK_UPDATE);

        // then
        StepVerifier.create(result).expectError(ApiErrorException.class).verify();
        server.verify(1, postRequestedFor(urlEqualTo("/updates")));
    }

    @Test
    public void sendUpdate__shouldTriggerCircuitBreaker_beforeTimeout() {
        // given
        server.stubFor(post("/updates").willReturn(aResponse().withStatus(500).withFixedDelay((int)
                CLIENT_CONFIG.timeout().plusSeconds(1).toMillis())));

        // when
        final Mono<Void> result = botClient.sendUpdate(LINK_UPDATE);

        // then
        StepVerifier.create(result)
                .expectErrorSatisfies(e -> assertThat(e).isInstanceOf(WebClientRequestException.class))
                .verify();
    }
}
