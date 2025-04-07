package backend.academy.scrapper.client.github;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.client.AbstractWireMockTest;
import backend.academy.scrapper.dto.github.GitHubActivity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class WebGitHubClientTest extends AbstractWireMockTest {

    @Mock
    private ScrapperConfig config;

    private WebGitHubClient gitHubClient;

    @BeforeEach
    public void setUp() {
        when(config.githubToken()).thenReturn("token");
        gitHubClient = new WebGitHubClient(server.baseUrl(), config);
    }

    @Test
    public void getIssueActivity__shouldThrowException_whenResponseIs404() {
        // given
        stubFor(get("/nonexistent/repo/issues").willReturn(aResponse().withStatus(404)));
        // when
        final Flux<GitHubActivity> result = gitHubClient.fetchUpdate(new String[] {"nonexistent", "repo"});
        // then
        StepVerifier.create(result).verifyError();
    }

    @Test
    public void getIssueActivity_shouldHandleMalformedResponse() {
        // given
        stubFor(get("/login/repo/issues")
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("malformed json")));

        // when
        final Flux<GitHubActivity> result = gitHubClient.fetchUpdate(new String[] {"login", "repo"});

        // then
        StepVerifier.create(result).verifyError();
    }
}
