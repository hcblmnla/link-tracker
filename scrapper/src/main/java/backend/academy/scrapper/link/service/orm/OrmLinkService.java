package backend.academy.scrapper.link.service.orm;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.link.service.LinkService;
import backend.academy.scrapper.link.service.orm.entity.Filter;
import backend.academy.scrapper.link.service.orm.entity.Link;
import backend.academy.scrapper.link.service.orm.entity.Tag;
import backend.academy.scrapper.link.service.orm.entity.User;
import backend.academy.scrapper.link.service.orm.entity.UserLink;
import backend.academy.scrapper.link.service.orm.repo.FilterRepository;
import backend.academy.scrapper.link.service.orm.repo.LinkRepository;
import backend.academy.scrapper.link.service.orm.repo.TagRepository;
import backend.academy.scrapper.link.service.orm.repo.UserLinkRepository;
import backend.academy.scrapper.link.service.orm.repo.UserRepository;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "ORM")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrmLinkService implements LinkService {

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    private final UserRepository userRepository;
    private final LinkRepository linkRepository;

    private final UserLinkRepository userLinkRepository;

    private final FilterRepository filterRepository;
    private final TagRepository tagRepository;

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

    private Timestamp nowTimestampInterval(final long updateInterval) {
        return Timestamp.valueOf(LocalDateTime.now(DEFAULT_ZONE).minusMinutes(updateInterval));
    }

    @Override
    public List<LinkDto> getLinksBatch(final int offset, final int batchSize, final long updateInterval) {
        return linkRepository.findByUpdatedAtBefore(nowTimestampInterval(updateInterval)).stream()
                .skip(offset)
                .limit(batchSize)
                .map(this::asLinkDto)
                .toList();
    }

    @Override
    @Transactional
    public void markLinkChecked(@NonNull final LinkDto linkDto) {
        linkRepository.findByUrl(linkDto.url().toString()).ifPresent(l -> {
            l.updatedAt(Timestamp.valueOf(LocalDateTime.now(DEFAULT_ZONE)));
            linkRepository.save(l);
        });
    }

    @Override
    public List<Long> getChatIds(@NonNull final LinkDto linkDto) {
        return linkRepository
                .findByUrl(linkDto.url().toString())
                .map(l -> userLinkRepository.findByLinkId(l.id()).stream()
                        .map(ul -> ul.user().id())
                        .toList())
                .orElse(List.of());
    }

    @Override
    @Transactional
    public void registerChat(final long id) {
        final User user = new User();
        user.id(id);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public boolean deleteChat(final long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    @Override
    public List<String> getTags(final long id) {
        return userRepository
                .findById(id)
                .map(user -> user.tags().stream().map(Tag::name).toList())
                .orElse(List.of());
    }

    @Override
    @Transactional
    public void addTag(final long id, final String tagName) {
        final Tag tag = tagRepository.findByName(tagName).orElseGet(() -> {
            final Tag t = new Tag();
            t.name(tagName);
            return tagRepository.save(t);
        });

        final User user = userRepository.findById(id).orElseThrow();

        if (!user.tags().contains(tag)) {
            user.tags().add(tag);
            userRepository.save(user);
        }
    }

    @Override
    public List<LinkDto> getLinks(final long id) {
        return userLinkRepository.findByUserId(id).stream()
                .map(ul -> asLinkDto(ul.link()))
                .toList();
    }

    @Override
    @Transactional
    public void addLink(final long id, @NonNull final LinkDto linkDto) {
        final Link link = linkRepository.findByUrl(linkDto.url().toString()).orElseGet(() -> {
            Link l = new Link();
            l.url(linkDto.url().toString());
            l.type(linkDto.type());
            l.uriVariables(List.of(linkDto.uriVariables()));
            l.updatedAt(Timestamp.valueOf(LocalDateTime.MIN));
            return linkRepository.save(l);
        });

        final User user = userRepository.findById(id).orElseThrow();
        final UserLink userLink = new UserLink();

        userLink.user(user);
        userLink.link(link);
        userLinkRepository.save(userLink);

        linkDto.tags().forEach(tagName -> {
            final Tag tag = new Tag();
            tag.name(tagName);

            final Tag updated = tagRepository.findByName(tagName).orElseGet(() -> tagRepository.save(tag));
            link.tags().add(updated);
        });

        linkDto.filters().forEach(filterName -> {
            final Filter filter = new Filter();
            filter.name(filterName);

            final Filter updated =
                    filterRepository.findByName(filterName).orElseGet(() -> filterRepository.save(filter));
            link.filters().add(updated);
        });
    }

    @Override
    @Nullable
    @Transactional
    public LinkDto removeLink(final long id, final URI url) {
        final Link link = linkRepository.findByUrl(url.toString()).orElse(null);
        if (link == null) {
            return null;
        }

        final UserLink userLink =
                userLinkRepository.findByUserIdAndLinkId(id, link.id()).orElseThrow();
        userLinkRepository.delete(userLink);

        final long remainingUsers = userLinkRepository.countByLinkId(link.id());
        if (remainingUsers == 0) {
            linkRepository.delete(link);
        }
        return asLinkDto(link);
    }
}
