package backend.academy.scrapper.controller;

import backend.academy.base.schema.scrapper.AddLinkRequest;
import backend.academy.base.schema.scrapper.AddTagRequest;
import backend.academy.base.schema.scrapper.LinkResponse;
import backend.academy.base.schema.scrapper.ListLinksResponse;
import backend.academy.base.schema.scrapper.RemoveLinkRequest;
import backend.academy.base.schema.scrapper.TagsResponse;
import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.exception.ChatNotExistsException;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.service.ScrapperService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimpleScrapperController implements ScrapperController {

    private final ScrapperService service;

    @Override
    public void registerChat(final Long id) {
        service.registerChat(id);
    }

    @Override
    public void deleteChat(final Long id) {
        if (!service.deleteChat(id)) {
            throw new ChatNotExistsException(id);
        }
    }

    @Override
    public TagsResponse getTags(final Long chatId) {
        return new TagsResponse(service.getTags(chatId));
    }

    @Override
    public void addTag(final Long chatId, final AddTagRequest request) {
        service.addTag(chatId, request);
    }

    @Override
    public ListLinksResponse getLinks(final Long chatId) {
        final List<LinkResponse> links = service.getLinks(chatId);
        return new ListLinksResponse(links, links.size());
    }

    @Override
    public LinkResponse addLink(final Long chatId, @NonNull final AddLinkRequest request) {
        service.addLink(chatId, request);
        return new LinkResponse(chatId, request.url(), request.tags(), request.filters());
    }

    @Override
    public LinkResponse removeLink(final Long chatId, @NonNull final RemoveLinkRequest request) {
        final LinkDto link = service.removeLink(chatId, request.url());
        if (link == null) {
            throw new LinkNotFoundException(request.url());
        }
        return new LinkResponse(chatId, link.url(), link.tags(), link.filters());
    }
}
