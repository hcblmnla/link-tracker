package backend.academy.bot.service;

import backend.academy.base.schema.ApiErrorException;
import backend.academy.base.schema.scrapper.AddLinkRequest;
import backend.academy.base.schema.scrapper.AddTagRequest;
import backend.academy.base.schema.scrapper.LinkResponse;
import backend.academy.base.schema.scrapper.ListLinksResponse;
import backend.academy.base.schema.scrapper.RemoveLinkRequest;
import backend.academy.base.schema.scrapper.TagsResponse;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.sender.NotificationConfig;
import backend.academy.bot.service.exception.AddTagServiceException;
import backend.academy.bot.service.exception.DeletingChatServiceException;
import backend.academy.bot.service.exception.LinksServiceException;
import backend.academy.bot.service.exception.TagsServiceException;
import backend.academy.bot.service.exception.TrackServiceException;
import backend.academy.bot.service.exception.UntrackServiceException;
import backend.academy.bot.state.TrackingLink;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotService {

    private final ScrapperClient client;
    private final NotificationConfig notificationConfig;

    public void registerChat(final long id) {
        client.registerChat(id).block();
        logInfo("Registered chat", id);
    }

    public void deleteChat(final long id) throws DeletingChatServiceException {
        try {
            client.deleteChat(id).block();
            logInfo("Deleted chat", id);
        } catch (final ApiErrorException e) {
            logError("Error deleting chat", id, e);
            throw new DeletingChatServiceException(e);
        }
    }

    public void setDigest(final long id) {
        notificationConfig.mode(NotificationConfig.Mode.DIGEST);
        logInfo("Set digest", id);
    }

    public void setInstantMode(final long id) {
        notificationConfig.mode(NotificationConfig.Mode.INSTANT);
        logInfo("Set instant mode", id);
    }

    public List<String> getTags(final long id) throws TagsServiceException {
        final TagsResponse response;
        try {
            response = client.getTags(id).block();
        } catch (final ApiErrorException e) {
            logError("Error getting tags", id, e);
            throw new TagsServiceException(e);
        }
        if (response == null) {
            logInfo("Tags not got", id);
            return List.of();
        }
        return response.tags();
    }

    public void addTag(final long id, @NonNull final String tag) throws AddTagServiceException {
        try {
            client.addTag(id, new AddTagRequest(tag)).block();
            logInfoBuilder("Added tag", id).addKeyValue("tag", tag).log();
        } catch (final ApiErrorException e) {
            logErrorBuilder("Error adding tag", id, e).addKeyValue("tag", tag).log();
            throw new AddTagServiceException(e);
        }
    }

    public List<LinkResponse> getLinks(final long id) throws LinksServiceException {
        final ListLinksResponse response;
        try {
            response = client.getLinks(id).block();
        } catch (final ApiErrorException e) {
            logError("Error getting links", id, e);
            throw new LinksServiceException(e);
        }
        if (response == null) {
            logInfo("No links found", id);
            return List.of();
        }
        final List<LinkResponse> links = response.links();
        logInfoBuilder("Links found", id).addKeyValue("links", links).log();
        return links;
    }

    public void addLink(final long id, @NonNull final TrackingLink link) throws TrackServiceException {
        final String logLink = link.url().toString();
        try {
            client.addLink(id, new AddLinkRequest(link.url(), link.tags(), link.filters()))
                    .block();
            logInfoBuilder("Added link", id).addKeyValue("link", logLink).log();
        } catch (final ApiErrorException e) {
            logErrorBuilder("Error adding link", id, e)
                    .addKeyValue("link", logLink)
                    .log();
            throw new TrackServiceException(e);
        }
    }

    public LinkResponse removeLink(final long id, final URI url) throws UntrackServiceException {
        try {
            final LinkResponse response =
                    client.removeLink(id, new RemoveLinkRequest(url)).block();
            logInfo("Removed link", id);
            return response;
        } catch (final ApiErrorException e) {
            logError("Error removing link", id, e);
            throw new UntrackServiceException(e);
        }
    }

    private void logInfo(final String message, final long id) {
        logInfoBuilder(message, id).log();
    }

    private LoggingEventBuilder logInfoBuilder(final String message, final long id) {
        return log.atInfo().setMessage(message).addKeyValue("id", id);
    }

    private void logError(final String message, final long id, @NonNull final Throwable e) {
        logErrorBuilder(message, id, e).log();
    }

    private LoggingEventBuilder logErrorBuilder(final String message, final long id, @NonNull final Throwable e) {
        return logInfoBuilder(message, id).setCause(e).addKeyValue("errorMessage", e.getMessage());
    }
}
