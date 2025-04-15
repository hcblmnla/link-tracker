package backend.academy.scrapper.service;

import backend.academy.base.schema.scrapper.AddLinkRequest;
import backend.academy.base.schema.scrapper.AddTagRequest;
import backend.academy.base.schema.scrapper.LinkResponse;
import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.link.service.LinkService;
import backend.academy.scrapper.service.github.GitHubSourceService;
import backend.academy.scrapper.service.stackoverflow.StackOverflowSourceService;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScrapperService {

    private final LinkService linkService;

    private final GitHubSourceService gitHubService;
    private final StackOverflowSourceService stackOverflowService;

    @Value("${batch.size}")
    private int batchSize;

    @Value("${batch.threads}")
    private int threads;

    @Value("${batch.awaiting}")
    private int awaiting;

    @Value("${update.minutes}")
    private long updateInterval;

    private void checkForUpdate(final LinkDto linkDto) {
        log.atInfo()
                .setMessage("Checking for update")
                .addKeyValue("dto", linkDto)
                .log();

        final boolean updated =
                switch (linkDto.type()) {
                    case GITHUB -> gitHubService.checkForUpdate(linkDto);
                    case STACKOVERFLOW -> stackOverflowService.checkForUpdate(linkDto);
                };

        if (updated) {
            linkService.markLinkChecked(linkDto);
        }
    }

    private void checkForUpdateImpl(final Consumer<LinkDto> action) {
        for (final LinkDto linkDto : linkService.getAllLinks(batchSize, updateInterval)) {
            action.accept(linkDto);
        }
    }

    public void checkForUpdate() {
        if (threads == 1) {
            checkForUpdateImpl(this::checkForUpdate);
            return;
        }
        try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
            checkForUpdateImpl(linkDto -> {
                final Future<?> ignored = executor.submit(() -> checkForUpdate(linkDto));
            });
            executor.shutdown();
            if (!executor.awaitTermination(awaiting, TimeUnit.MINUTES)) {
                log.error("Timed out waiting for update checking");
            }
        } catch (InterruptedException e) {
            log.error("Interrupted while checking for update", e);
        }
    }

    public void registerChat(final long id) {
        linkService.registerChat(id);
        log.atInfo().setMessage("Registering chat").addKeyValue("id", id).log();
    }

    public boolean deleteChat(final long id) {
        final boolean deleted = linkService.deleteChat(id);
        if (deleted) {
            log.atInfo().setMessage("Deleted chat").addKeyValue("id", id).log();
        } else {
            log.atInfo()
                    .setMessage("Failed to delete chat")
                    .addKeyValue("id", id)
                    .log();
        }
        return deleted;
    }

    public List<String> getTags(final long id) {
        final List<String> tags = linkService.getTags(id);
        log.atInfo()
                .setMessage("Getting tags")
                .addKeyValue("id", id)
                .addKeyValue("tags", tags)
                .log();
        return tags;
    }

    public void addTag(final long id, final AddTagRequest request) {
        linkService.addTag(id, request.name());
        log.atInfo().setMessage("Added tag").addKeyValue("id", id).log();
    }

    public List<LinkResponse> getLinks(final long id) {
        final List<LinkDto> links = linkService.getLinks(id);

        log.atInfo()
                .setMessage("Getting links")
                .addKeyValue("id", id)
                .addKeyValue("links", links)
                .log();

        return links.stream()
                .map(dto -> new LinkResponse(id, dto.url(), dto.tags(), dto.filters()))
                .toList();
    }

    public void addLink(final long id, @NonNull final AddLinkRequest request) {
        final LinkDto dto = LinkDto.of(request.url(), request.tags(), request.filters());
        movingLinkLog("Adding link", id, dto);
        linkService.addLink(id, dto);
    }

    public LinkDto removeLink(final long id, final URI url) {
        final LinkDto dto = linkService.removeLink(id, url);
        movingLinkLog("Removed link", id, dto);
        return dto;
    }

    private void movingLinkLog(final String message, final long id, final LinkDto dto) {
        log.atInfo()
                .setMessage(message)
                .addKeyValue("id", id)
                .addKeyValue("dto", dto)
                .log();
    }
}
