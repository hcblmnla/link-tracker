package backend.academy.scrapper.repository.sql;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.repository.LinkRepositoryTest;
import backend.academy.scrapper.validation.LinkType;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.access-type=SQL")
class SqlLinkRepositoryTest extends LinkRepositoryTest {

    @Autowired
    private SqlLinkRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @AfterEach
    void truncateTables() {
        jdbc.update("truncate users, links, tags, filters, user_links, link_tags, link_filters, "
                + "user_tags restart identity cascade");
    }

    @Test
    void registerDeleteChat_shouldAddRemoveUser() {
        // given
        repository.registerChat(1L);
        assertThat(jdbc.queryForObject("select count(*) from users", Integer.class))
                .isEqualTo(1);

        // when-then
        final boolean deleted = repository.deleteChat(1L);
        assertThat(deleted).isTrue();
        assertThat(jdbc.queryForObject("select count(*) from users", Integer.class))
                .isZero();
    }

    @Test
    void addRemoveLink_shouldAddAndRemoveLink() {
        // given
        repository.registerChat(1L);
        final LinkDto link = new LinkDto(
                URL, List.of("java"), List.of("open-source"), LinkType.GITHUB, new String[] {"test", "repo"});

        // when
        repository.addLink(1L, link);
        assertThat(repository.getLinks(1L)).hasSize(1);

        // then
        final LinkDto removed = repository.removeLink(1L, URL);
        assertThat(removed).isNotNull();
        assertThat(repository.getLinks(1L)).isEmpty();
    }

    @Test
    void getLinksBatch_shouldReturnLinksOlderThanInterval() {
        // given
        jdbc.update(
                "insert into links (url, type, uri_variables, updated_at) values (?, ?, ?, now() - interval '2 hours')",
                SO,
                "STACKOVERFLOW",
                new String[] {});

        // when
        final List<LinkDto> batch = repository.getLinksBatch(0, 10);

        // then
        assertThat(batch).hasSize(1);
        assertThat(batch.getFirst().url()).hasToString(SO);
    }

    @Test
    void markLinkChecked_shouldUpdateTimestamp() {
        // given
        jdbc.update(
                "insert into links (url, type, uri_variables) values (?, ?, ?)", SO, "STACKOVERFLOW", new String[] {});

        // when
        final LinkDto link = new LinkDto(URI.create(SO), List.of(), List.of(), LinkType.STACKOVERFLOW, new String[] {});
        repository.markLinkChecked(link);

        // then
        final String updatedAt = jdbc.queryForObject("select updated_at from links where url = ?", String.class, SO);
        assertThat(updatedAt).isNotEqualTo("-infinity");
    }

    @Test
    void addGetTags_shouldManageTagsForUser() {
        // given
        repository.registerChat(1L);
        repository.addTag(1L, "spring");
        repository.addTag(1L, "jpa");

        // when-then
        final List<String> tags = repository.getTags(1L);
        assertThat(tags).containsExactly("spring", "jpa");
    }

    @Test
    void getChatIds_shouldReturnUserIdsForLink() {
        // given
        repository.registerChat(1L);
        jdbc.update("insert into links (url, type, uri_variables) values (?, ?, ?)", LINK, "GITHUB", new String[] {
            "test", "repo"
        });
        jdbc.update("insert into user_links (user_id, link_id) values (1, 1)");

        // when
        final LinkDto link = new LinkDto(URL, List.of(), List.of(), LinkType.GITHUB, new String[] {"test", "repo"});

        // then
        final List<Long> chatIds = repository.getChatIds(link);
        assertThat(chatIds).containsExactly(1L);
    }
}
