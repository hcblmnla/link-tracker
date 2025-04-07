package backend.academy.scrapper.client.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

import backend.academy.base.schema.ApiErrorException;
import backend.academy.scrapper.client.AbstractWireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class WebBotClientTest extends AbstractWireMockTest {

    private BotClient botClient;

    @BeforeEach
    public void setUp() {
        botClient = new WebBotClient(server.baseUrl());
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
}
