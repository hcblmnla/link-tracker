package backend.academy.bot.client;

import backend.academy.base.schema.scrapper.AddLinkRequest;
import backend.academy.base.schema.scrapper.AddTagRequest;
import backend.academy.base.schema.scrapper.LinkResponse;
import backend.academy.base.schema.scrapper.ListLinksResponse;
import backend.academy.base.schema.scrapper.RemoveLinkRequest;
import backend.academy.base.schema.scrapper.TagsResponse;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Mono;

public interface ScrapperClient {

    String TG_CHAT_URI = "/tg-chat/{id}";
    String LINKS_URI = "/links";
    String TG_CHAT_ID_HEADER = "Tg-Chat-Id";

    String TAGS_URI = "/tags";

    Mono<Void> registerChat(long id);

    Mono<Void> deleteChat(long id);

    Mono<TagsResponse> getTags(long chatId);

    Mono<Void> addTag(long chatId, @NonNull AddTagRequest request);

    Mono<ListLinksResponse> getLinks(long chatId);

    Mono<LinkResponse> addLink(long chatId, @NonNull AddLinkRequest request);

    Mono<LinkResponse> removeLink(long chatId, @NonNull RemoveLinkRequest request);
}
