package backend.academy.scrapper.client.stackoverflow;

import backend.academy.base.client.AbstractWebClient;
import backend.academy.scrapper.ScrapperConfig;
import backend.academy.scrapper.client.AbstractWebSourceClient;
import backend.academy.scrapper.dto.stackoverflow.StackOverflowAnswers;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebStackOverflowClient extends AbstractWebSourceClient<StackOverflowAnswers> {

    public WebStackOverflowClient(
            @Value("${server.stackoverflow-base}") final String baseUrl, final ScrapperConfig config) {
        super(
                WebClient.builder()
                        .baseUrl(baseUrl)
                        .defaultUriVariables(Map.of(
                                "key", config.stackOverflow().key(),
                                "access_token", config.stackOverflow().accessToken()))
                        .defaultHeaders(hs -> hs.setAccept(AbstractWebClient.ACCEPTABLE_MEDIA_TYPES))
                        .build(),
                "/{questionId}/answers/?site=stackoverflow&body=withbody",
                StackOverflowAnswers.class);
    }
}
