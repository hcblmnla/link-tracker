package backend.academy.scrapper.client.bot;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

import backend.academy.scrapper.client.AbstractWireMockTest;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class ClientRateLimitTest extends AbstractWireMockTest implements ClientTestConfig {

    private BotClient botClient;

    @BeforeEach
    public void setUp() {
        botClient = new WebBotClient(server.baseUrl(), CLIENT_CONFIG, DDOS_RATE_LIMITER);
    }

    @Test
    public void sendUpdate__shouldReturn429_whenRateLimitExceeded() {
        // given
        server.stubFor(post("/updates").willReturn(ok()));

        // when-then
        IntStream.range(0, 10).forEach(ignored -> StepVerifier.create(botClient.sendUpdate(LINK_UPDATE))
                .verifyComplete());

        IntStream.range(0, 10).forEach(ignored -> StepVerifier.create(botClient.sendUpdate(LINK_UPDATE))
                .expectError(RequestNotPermitted.class)
                .verify());
    }
}
