package backend.academy.bot.client;

import backend.academy.base.client.AbstractWebClient;
import backend.academy.base.schema.ApiErrorException;
import backend.academy.base.schema.ApiErrorResponse;
import backend.academy.base.schema.scrapper.AddLinkRequest;
import backend.academy.base.schema.scrapper.AddTagRequest;
import backend.academy.base.schema.scrapper.LinkResponse;
import backend.academy.base.schema.scrapper.ListLinksResponse;
import backend.academy.base.schema.scrapper.RemoveLinkRequest;
import backend.academy.base.schema.scrapper.TagsResponse;
import backend.academy.bot.link.service.NotificationMode;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WebScrapperClient extends AbstractWebClient implements ScrapperClient {

    public WebScrapperClient(@Value("${server.base-url}") final String baseUrl) {
        super(baseUrl);
    }

    private Mono<Void> moveChat(final long id, final HttpMethod method) {
        return webClient
                .method(method)
                .uri(TG_CHAT_URI, id)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(ApiErrorResponse.class)
                        .flatMap(error -> Mono.error(new ApiErrorException(error))))
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<Void> registerChat(final long id) {
        return moveChat(id, HttpMethod.POST);
    }

    @Override
    public Mono<Void> deleteChat(final long id) {
        return moveChat(id, HttpMethod.DELETE);
    }

    @Override
    public Mono<Void> setMode(final long chatId, @NonNull final NotificationMode mode) {
        return webClient
                .post()
                .uri(mode.toUri())
                .header(TG_CHAT_ID_HEADER, Long.toString(chatId))
                .retrieve()
                .bodyToMono(Void.class);
    }

    private <T> Mono<T> getListOf(final long chatId, final String uri, final Class<T> token) {
        return webClient
                .get()
                .uri(uri)
                .header(TG_CHAT_ID_HEADER, Long.toString(chatId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(ApiErrorResponse.class)
                        .flatMap(error -> Mono.error(new ApiErrorException(error))))
                .bodyToMono(token);
    }

    @Override
    public Mono<TagsResponse> getTags(final long chatId) {
        return getListOf(chatId, TAGS_URI, TagsResponse.class);
    }

    private <T> Mono<T> moveT(
            final long chatId, final Object body, final HttpMethod method, final String uri, final Class<T> token) {
        return webClient
                .method(method)
                .uri(uri)
                .header(TG_CHAT_ID_HEADER, Long.toString(chatId))
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(ApiErrorResponse.class)
                        .flatMap(error -> Mono.error(new ApiErrorException(error))))
                .bodyToMono(token);
    }

    @Override
    public Mono<Void> addTag(final long chatId, final @NonNull AddTagRequest request) {
        return moveT(chatId, request, HttpMethod.POST, TAGS_URI, Void.class);
    }

    @Override
    public Mono<ListLinksResponse> getLinks(final long chatId) {
        return getListOf(chatId, LINKS_URI, ListLinksResponse.class);
    }

    private Mono<LinkResponse> moveLink(final long chatId, final Object body, final HttpMethod method) {
        return moveT(chatId, body, method, LINKS_URI, LinkResponse.class);
    }

    @Override
    public Mono<LinkResponse> addLink(final long chatId, @NotNull final AddLinkRequest request) {
        return moveLink(chatId, request, HttpMethod.POST);
    }

    @Override
    public Mono<LinkResponse> removeLink(final long chatId, @NotNull final RemoveLinkRequest request) {
        return moveLink(chatId, request, HttpMethod.DELETE);
    }
}
