package backend.academy.scrapper.repository.orm;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.repository.orm.entity.Filter;
import backend.academy.scrapper.repository.orm.entity.Link;
import backend.academy.scrapper.repository.orm.entity.Tag;
import backend.academy.scrapper.repository.orm.entity.User;
import backend.academy.scrapper.repository.orm.entity.UserLink;
import backend.academy.scrapper.repository.orm.repo.FilterRepo;
import backend.academy.scrapper.repository.orm.repo.LinkRepo;
import backend.academy.scrapper.repository.orm.repo.TagRepo;
import backend.academy.scrapper.repository.orm.repo.UserLinkRepo;
import backend.academy.scrapper.repository.orm.repo.UserRepo;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
@RequiredArgsConstructor
public class OrmLinkRepository implements LinkRepository {

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    private final UserRepo userRepo;
    private final LinkRepo linkRepo;

    private final UserLinkRepo userLinkRepo;

    private final FilterRepo filterRepo;
    private final TagRepo tagRepo;

    @Value("${update.minutes}")
    private long updateInterval;

    private <T> List<String> asParamList(
            final List<? extends T> params, final Function<? super T, String> paramToString) {
        return params.stream().map(paramToString).toList();
    }

    private LinkDto asLinkDto(final Link link) {
        return new LinkDto(
                URI.create(link.url()),
                asParamList(link.tags(), Tag::name),
                asParamList(link.filters(), Filter::name),
                link.type(),
                link.uriVariables().toArray(new String[0]));
    }

    private Timestamp nowTimestampInterval() {
        return Timestamp.valueOf(LocalDateTime.now(DEFAULT_ZONE).minusMinutes(updateInterval));
    }

    @Override
    public List<LinkDto> getLinksBatch(final int offset, final int batchSize) {
        return linkRepo.findByUpdatedAtBefore(nowTimestampInterval()).stream()
                .skip(offset)
                .limit(batchSize)
                .map(this::asLinkDto)
                .toList();
    }

    @Override
    @Transactional
    public void markLinkChecked(@NonNull final LinkDto linkDto) {
        linkRepo.findByUrl(linkDto.url().toString()).ifPresent(l -> {
            l.updatedAt(Timestamp.valueOf(LocalDateTime.now(DEFAULT_ZONE)));
            linkRepo.save(l);
        });
    }

    @Override
    public List<Long> getChatIds(@NonNull final LinkDto linkDto) {
        return linkRepo.findByUrl(linkDto.url().toString())
                .map(l -> userLinkRepo.findByLinkId(l.id()).stream()
                        .map(ul -> ul.user().id())
                        .toList())
                .orElse(List.of());
    }

    @Override
    @Transactional
    public void registerChat(final long id) {
        final User user = new User();
        user.id(id);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public boolean deleteChat(final long id) {
        if (!userRepo.existsById(id)) {
            return false;
        }
        userRepo.deleteById(id);
        return true;
    }

    @Override
    public List<String> getTags(final long id) {
        return userRepo.findById(id)
                .map(user -> user.tags().stream().map(Tag::name).toList())
                .orElse(List.of());
    }

    @Override
    @Transactional
    public void addTag(final long id, final String tagName) {
        final Tag tag = tagRepo.findByName(tagName).orElseGet(() -> {
            final Tag t = new Tag();
            t.name(tagName);
            return tagRepo.save(t);
        });

        final User user = userRepo.findById(id).orElseThrow();

        if (!user.tags().contains(tag)) {
            user.tags().add(tag);
            userRepo.save(user);
        }
    }

    @Override
    public List<LinkDto> getLinks(final long id) {
        return userLinkRepo.findByUserId(id).stream()
                .map(ul -> asLinkDto(ul.link()))
                .toList();
    }

    @Override
    @Transactional
    public void addLink(final long id, @NonNull final LinkDto linkDto) {
        final Link link = linkRepo.findByUrl(linkDto.url().toString()).orElseGet(() -> {
            Link l = new Link();
            l.url(linkDto.url().toString());
            l.type(linkDto.type());
            l.uriVariables(List.of(linkDto.uriVariables()));
            l.updatedAt(Timestamp.valueOf(LocalDateTime.MIN));
            return linkRepo.save(l);
        });

        final User user = userRepo.findById(id).orElseThrow();
        final UserLink userLink = new UserLink();

        userLink.user(user);
        userLink.link(link);
        userLinkRepo.save(userLink);

        linkDto.tags().forEach(tagName -> {
            final Tag tag = new Tag();
            tag.name(tagName);

            final Tag updated = tagRepo.findByName(tagName).orElseGet(() -> tagRepo.save(tag));
            link.tags().add(updated);
        });

        linkDto.filters().forEach(filterName -> {
            final Filter filter = new Filter();
            filter.name(filterName);

            final Filter updated = filterRepo.findByName(filterName).orElseGet(() -> filterRepo.save(filter));
            link.filters().add(updated);
        });
    }

    @Override
    @Nullable
    @Transactional
    public LinkDto removeLink(final long id, final URI url) {
        final Link link = linkRepo.findByUrl(url.toString()).orElse(null);
        if (link == null) {
            return null;
        }

        final UserLink userLink =
                userLinkRepo.findByUserIdAndLinkId(id, link.id()).orElseThrow();
        userLinkRepo.delete(userLink);

        final long remainingUsers = userLinkRepo.countByLinkId(link.id());
        if (remainingUsers == 0) {
            linkRepo.delete(link);
        }
        return asLinkDto(link);
    }
}
