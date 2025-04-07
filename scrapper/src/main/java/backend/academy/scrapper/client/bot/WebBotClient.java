package backend.academy.scrapper.client.bot;

import backend.academy.base.client.AbstractWebClient;
import backend.academy.base.schema.ApiErrorException;
import backend.academy.base.schema.ApiErrorResponse;
import backend.academy.base.schema.bot.LinkUpdate;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WebBotClient extends AbstractWebClient implements BotClient {

    private static final String UPDATES = "/updates";

    public WebBotClient(@Value("${server.base-url}") final String baseUrl) {
        super(baseUrl);
    }

    @Override
    public Mono<Void> sendUpdate(@NonNull final LinkUpdate update) {
        return webClient
                .post()
                .uri(UPDATES)
                .bodyValue(update)
                .retrieve()
                .onStatus(code -> code.isSameCodeAs(HttpStatus.BAD_REQUEST), response -> response.bodyToMono(
                                ApiErrorResponse.class)
                        .flatMap(error -> Mono.error(new ApiErrorException(error))))
                .bodyToMono(Void.class);
    }
}
