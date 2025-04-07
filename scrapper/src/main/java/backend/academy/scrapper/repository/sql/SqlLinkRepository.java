package backend.academy.scrapper.repository.sql;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.repository.LinkRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ConditionalOnProperty(prefix = "app", name = "access-type", havingValue = "SQL")
@RequiredArgsConstructor
@Slf4j
public class SqlLinkRepository implements LinkRepository {

    private final JdbcTemplate jdbc;
    private final LinkDtoMapper linkDtoMapper;

    @Value("${update.interval}")
    private String updateInterval;

    @Override
    public List<LinkDto> getLinksBatch(final int offset, final int batchSize) {
        final String sql =
                """
                select * from link_dto_view
                where updated_at < now() - cast(? as interval)
                order by updated_at
                offset ? limit ?
                """;

        return jdbc.query(sql, linkDtoMapper, updateInterval, offset, batchSize);
    }

    @Override
    @Transactional
    public void markLinkChecked(@NonNull final LinkDto linkDto) {
        jdbc.update(
                "update links set updated_at = now() where url = ?",
                linkDto.url().toString());
    }

    @Override
    public List<Long> getChatIds(@NonNull final LinkDto linkDto) {
        final String sql =
                """
                select user_id
                from user_links
                join links on link_id = id
                where url = ?
                """;

        return jdbc.queryForList(sql, Long.class, linkDto.url().toString());
    }

    @Override
    @Transactional
    public void registerChat(final long id) {
        jdbc.update("insert into users values (?) on conflict do nothing", id);
    }

    @Override
    @Transactional
    public boolean deleteChat(final long id) {
        final int deleted = jdbc.update("delete from users where id = ?", id);
        return deleted > 0;
    }

    @Override
    public List<String> getTags(final long id) {
        final String sql =
                """
                select name
                from tags
                join user_tags on tag_id = id
                where user_id = ?
                """;

        return jdbc.queryForList(sql, String.class, id);
    }

    @Override
    @Transactional
    public void addTag(final long id, final String tag) {
        final Long tagId;
        final List<Long> existing = jdbc.queryForList("select id from tags where name = ?", Long.class, tag);

        if (!existing.isEmpty()) {
            tagId = existing.getFirst();
        } else {
            final List<Long> inserted = jdbc.query(
                    "insert into tags (name) values (?) returning id", (rs, ignored) -> rs.getLong("id"), tag);

            if (inserted.isEmpty()) {
                log.error("Cannot add tag: {}", tag);
                return;
            }
            tagId = inserted.getFirst();
        }

        final String sql = "insert into user_tags (user_id, tag_id) values (?, ?) on conflict do nothing";
        jdbc.update(sql, id, tagId);
    }

    @Override
    public List<LinkDto> getLinks(final long id) {
        return jdbc.query(
                "select * from link_dto_view join user_links on id = link_id where user_id = ?", linkDtoMapper, id);
    }

    @SuppressFBWarnings("SQL_INJECTION_SPRING_JDBC") // private method
    private void addParam(final String param, final Long linkId, final String paramName) {
        final String table = paramName + "s";

        final Long paramId;
        final List<Long> existing = jdbc.queryForList("select id from " + table + " where name = ?", Long.class, param);

        if (!existing.isEmpty()) {
            paramId = existing.getFirst();
        } else {
            final List<Long> inserted = jdbc.query(
                    "insert into " + table + " (name) values (?) returning id",
                    (rs, ignored) -> rs.getLong("id"),
                    param);

            if (inserted.isEmpty()) {
                log.error("Cannot add {}: {}", paramName, param);
                return;
            }
            paramId = inserted.getFirst();
        }

        final String linkTable = "link_" + table;
        final String paramIdColumn = paramName + "_id";

        final String sql =
                "insert into " + linkTable + " (link_id, " + paramIdColumn + ") values (?, ?) on conflict do nothing";

        jdbc.update(sql, linkId, paramId);
    }

    @Override
    @Transactional
    public void addLink(final long userId, @NonNull final LinkDto linkDto) {
        final Long linkId;
        final List<Long> found = jdbc.queryForList(
                "select id from links where url = ?", Long.class, linkDto.url().toString());

        if (!found.isEmpty()) {
            linkId = found.getFirst();
        } else {
            final List<Long> inserted = jdbc.query(
                    "insert into links (url, type, uri_variables) values (?, ?, ?) returning id",
                    (rs, ignored) -> rs.getLong("id"),
                    linkDto.url().toString(),
                    linkDto.type().toString(),
                    linkDto.uriVariables());

            if (inserted.isEmpty()) {
                log.error("Failed to insert or find link: {}", linkDto.url());
                return;
            }
            linkId = inserted.getFirst();
        }

        jdbc.update("insert into user_links (user_id, link_id) values (?, ?) on conflict do nothing", userId, linkId);

        linkDto.tags().forEach(tag -> addParam(tag, linkId, "tag"));
        linkDto.filters().forEach(filter -> addParam(filter, linkId, "filter"));
    }

    @Override
    @Nullable
    @Transactional
    public LinkDto removeLink(final long userId, final URI url) {
        final List<Long> ids = jdbc.queryForList("select id from links where url = ?", Long.class, url.toString());
        if (ids.isEmpty()) {
            return null;
        }

        final Long linkId = ids.getFirst();
        jdbc.update("delete from user_links where user_id = ? and link_id = ?", userId, linkId);

        final LinkDto dto = jdbc.query("select * from link_dto_view where id = ?", linkDtoMapper, linkId).stream()
                .findFirst()
                .orElse(null);

        final Integer alive =
                jdbc.queryForObject("select count(*) from user_links where link_id = ?", Integer.class, linkId);

        if (alive == null || alive == 0) {
            jdbc.update("delete from links where id = ?", linkId);
        }
        return dto;
    }
}
