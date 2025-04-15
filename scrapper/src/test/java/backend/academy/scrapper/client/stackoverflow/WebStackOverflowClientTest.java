package backend.academy.scrapper.client.stackoverflow;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.client.AbstractWireMockTest;
import backend.academy.scrapper.dto.stackoverflow.StackOverflowAnswers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class WebStackOverflowClientTest extends AbstractWireMockTest {

    @Mock
    private ScrapperConfig config;

    private WebStackOverflowClient stackOverflowClient;

    @BeforeEach
    public void setUp() {
        when(config.stackOverflow()).thenReturn(new ScrapperConfig.StackOverflowCredentials("key", "token"));
        stackOverflowClient = new WebStackOverflowClient(server.baseUrl(), config);
    }

    @Test
    public void getAnswers__shouldReturnAnswers_whenResponseIs200() {
        // given
        final String body =
                """
                {
                    "items":
                    [{
                        "answer_id": 42,
                        "body": "abc def"
                    },
                    {
                        "answer_id": 43,
                        "body": "zxc zxc zxc"
                    }]
                }
                """;

        server.stubFor(get(urlPathEqualTo("/1/answers/"))
                .withQueryParam("site", equalTo("stackoverflow"))
                .withQueryParam("body", equalTo("withbody"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(body)));

        final Flux<StackOverflowAnswers> result = stackOverflowClient.fetchUpdate(new String[] {"1"});

        // then
        StepVerifier.create(result)
                .expectNextMatches(answers -> answers.items().size() == 2
                        && answers.items().getFirst() != null
                        && "42".equals(answers.items().getFirst().answerId()))
                .verifyComplete();
    }

    @Test
    public void getAnswers__shouldThrowException_whenResponseIs404() {
        server.stubFor(get(urlPathEqualTo("/1/answers/"))
                .withQueryParam("site", equalTo("stackoverflow"))
                .withQueryParam("body", equalTo("withbody"))
                .willReturn(aResponse().withStatus(404)));

        final Flux<StackOverflowAnswers> result = stackOverflowClient.fetchUpdate(new String[] {"1"});

        StepVerifier.create(result).expectError().verify();
    }
}
