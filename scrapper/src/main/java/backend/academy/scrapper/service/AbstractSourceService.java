package backend.academy.scrapper.service;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.scrapper.client.SourceClient;
import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.link.service.LinkService;
import backend.academy.scrapper.metrics.LinksScrapeTimerService;
import backend.academy.scrapper.notification.digest.RedisDigestStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;

/**
 * Common service code for sources like:
 *
 * <ul>
 *   <li>{@code GitHub}
 *   <li>{@code StackOverflow}
 * </ul>
 *
 * <p>Provides API for notification of source changes.
 *
 * @param <S> source dto type
 * @author alnmlbch
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSourceService<S> {

    private final LinkService linkService;
    private final BotClient botClient;
    private final SourceClient<S> sourceClient;

    private final RedisDigestStorage redisDigestStorage;

    private final Map<LinkDto, S> repository = new HashMap<>();
    private final AtomicInteger counter = new AtomicInteger();

    private final LinksScrapeTimerService scrapeTimerService;

    @Value("${update.message.max-size}")
    private int maxBodySize;

    @Value("${update.message.continue-tag}")
    private String continueTag;

    @SuppressWarnings("DataFlowIssue")
    private void updateSourceLink(@NonNull final LinkDto dto, final String message) {
        final LinkUpdate update = new LinkUpdate(1L, dto.url(), message, linkService.getChatIds(dto));
        try {
            final List<Long> instant = new ArrayList<>();
            final List<Long> digest = new ArrayList<>();

            update.chatIds().forEach(chatId -> {
                switch (linkService.getMode(chatId)) {
                    case INSTANT -> instant.add(chatId);
                    case DIGEST -> digest.add(chatId);

                    default -> log.atInfo()
                            .addKeyValue("chatId", chatId)
                            .setMessage("Got null mode")
                            .log();
                }
            });

            botClient.sendUpdate(update.updateChatIds(instant)).subscribe();
            redisDigestStorage.waitUpdate(update.updateChatIds(digest));

            log.atInfo().setMessage("Updated link").addKeyValue("dto", dto).log();
        } catch (final Exception e) {
            logError("Error updating link", e);
        }
    }

    public int getAmountOfLinks() {
        return counter.getAndSet(0);
    }

    public boolean checkForUpdate(@NonNull final LinkDto dto) {
        counter.incrementAndGet();
        final S nextActivity;
        try {
            nextActivity = scrapeTimerService.recordScrape(
                    dto.type(),
                    () -> sourceClient.fetchUpdate(dto.uriVariables()).blockFirst());
        } catch (final Exception e) {
            logError("Error checking for update", e);
            return false;
        }
        final S prevActivity = repository.get(dto);
        if (prevActivity == null) {
            repository.put(dto, nextActivity);
            return false;
        }
        final String diff = calculateDiff(prevActivity, nextActivity);
        if (diff != null) {
            repository.put(dto, nextActivity);
            if (isFiltered(dto, nextActivity)) {
                return false;
            }
            updateSourceLink(dto, diff);
            return true;
        }
        return false;
    }

    protected abstract boolean isFiltered(LinkDto dto, S activity);

    protected abstract Object[] diffMessageArgs(S activity);

    @Nullable
    public String calculateDiff(final S prev, @Nullable final S next) {
        if (prev.equals(next)) {
            return null;
        }
        return """
            Новое событие вида %s:
            1. Название – %s
            2. Пользователь – %s
            3. Время создания – %s
            4. Описание – %s
            """
                .formatted(diffMessageArgs(next));
    }

    protected String croppedBody(final String body) {
        if (body == null) {
            return "отсутствует";
        }
        return body.length() < maxBodySize ? body : body.substring(0, maxBodySize) + continueTag;
    }

    private void logError(final String message, @NonNull final Exception e) {
        log.atInfo()
                .setMessage(message)
                .setCause(e)
                .addKeyValue("errorMessage", e.getMessage())
                .log();
    }
}
