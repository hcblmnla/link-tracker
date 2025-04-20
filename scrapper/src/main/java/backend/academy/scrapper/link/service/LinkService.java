package backend.academy.scrapper.link.service;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.notification.NotificationMode;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface LinkService {

    List<LinkDto> getLinksBatch(int offset, int batchSize, long updateInterval);

    @Transactional
    void markLinkChecked(@NonNull LinkDto linkDto);

    List<Long> getChatIds(@NonNull LinkDto linkDto);

    @Transactional
    void registerChat(long id);

    @Transactional
    boolean deleteChat(long id);

    @Nullable
    default NotificationMode getMode(final long chatId) {
        return NotificationMode.INSTANT;
    }

    @Transactional
    default void setMode(final long chatId, @NonNull final NotificationMode mode) {}

    List<String> getTags(long id);

    @Transactional
    void addTag(long id, String tag);

    List<LinkDto> getLinks(long id);

    @Transactional
    void addLink(long id, @NonNull LinkDto linkDto);

    @Nullable
    @Transactional
    LinkDto removeLink(long id, URI url);

    default Iterable<LinkDto> getAllLinks(final int batchSize, final long updateInterval) {
        return () -> new Iterator<>() {

            private int offset = 0;
            private List<LinkDto> currentBatch = new ArrayList<>();
            private int index = 0;

            @Override
            public boolean hasNext() {
                if (index < currentBatch.size()) {
                    return true;
                }
                currentBatch = getLinksBatch(offset, batchSize, updateInterval);
                if (currentBatch.isEmpty()) {
                    return false;
                }
                offset += batchSize;
                index = 0;
                return true;
            }

            @Override
            public LinkDto next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more links in batches");
                }
                return currentBatch.get(index++);
            }
        };
    }
}
