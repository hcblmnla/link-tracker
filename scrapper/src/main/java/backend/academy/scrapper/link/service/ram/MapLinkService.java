package backend.academy.scrapper.link.service.ram;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.link.service.LinkService;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "RAM")
public class MapLinkService implements LinkService {

    private final Map<LinkDto, Set<Long>> map = new HashMap<>();
    private final Map<Long, Set<String>> tags = new HashMap<>();

    @Override
    public List<LinkDto> getLinksBatch(final int offset, final int batchSize, final long updateInterval) {
        return map.keySet().stream().skip(offset).limit(batchSize).toList();
    }

    @Override
    public void markLinkChecked(@NonNull final LinkDto linkDto) {}

    @Override
    public List<Long> getChatIds(@NonNull final LinkDto linkDto) {
        return map.getOrDefault(linkDto, Set.of()).stream().toList();
    }

    @Override
    public void registerChat(final long id) {}

    @Override
    public boolean deleteChat(final long id) {
        return map.values().stream().anyMatch(ids -> ids.remove(id));
    }

    @Override
    public List<String> getTags(final long id) {
        return tags.getOrDefault(id, Collections.emptySet()).stream().toList();
    }

    @Override
    public void addTag(final long id, final String tag) {
        tags.computeIfAbsent(id, ignored -> new HashSet<>()).add(tag);
    }

    @Override
    public List<LinkDto> getLinks(final long id) {
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().contains(id))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public void addLink(final long id, @NonNull final LinkDto linkDto) {
        final Set<Long> ids = map.get(linkDto);
        if (ids == null) {
            map.put(linkDto, new HashSet<>(List.of(id)));
        } else {
            ids.add(id);
        }
    }

    @Override
    @Nullable
    public LinkDto removeLink(final long id, final URI url) {
        final LinkDto dto = map.keySet().stream()
                .filter(link -> link.url().equals(url))
                .findFirst()
                .orElse(null);

        if (dto == null) {
            return null;
        }

        map.get(dto).remove(id);
        return dto;
    }
}
