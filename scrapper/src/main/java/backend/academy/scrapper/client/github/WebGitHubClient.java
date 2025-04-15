package backend.academy.scrapper.client.github;

import backend.academy.base.client.AbstractWebClient;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.client.AbstractWebSourceClient;
import backend.academy.scrapper.dto.github.GitHubActivity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebGitHubClient extends AbstractWebSourceClient<GitHubActivity> {

    public WebGitHubClient(@Value("${server.github-base}") final String baseUrl, final ScrapperConfig config) {
        super(
                WebClient.builder()
                        .baseUrl(baseUrl)
                        .defaultHeaders(hs -> {
                            hs.setBearerAuth(config.githubToken());
                            hs.setAccept(AbstractWebClient.ACCEPTABLE_MEDIA_TYPES);
                        })
                        .build(),
                "/{login}/{repo}/issues",
                GitHubActivity.class);
    }
}
