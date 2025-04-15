package backend.academy.scrapper.link.service.sql;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.link.service.LinkServiceTest;
import backend.academy.scrapper.validation.LinkType;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@TestPropertySource(properties = "app.access-type=SQL")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SqlLinkServiceTest extends LinkServiceTest {

    @Autowired
    private SqlLinkService repository;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void registerDeleteChat_shouldAddRemoveUser() {
        // given
        repository.registerChat(1L);

        // when-then
        assertThat(jdbc.queryForObject("select count(*) from users", Integer.class))
                .isEqualTo(1);

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
    @Sql(scripts = "/insert-old-link.sql")
    void getLinksBatch_shouldReturnLinksOlderThanInterval() {
        // given-when
        final List<LinkDto> batch = repository.getLinksBatch(0, 10, 60);

        // then
        assertThat(batch).hasSize(1);
        assertThat(batch.getFirst().url()).hasToString(SO);
    }

    @Test
    @Sql(scripts = "/insert-new-link.sql")
    void markLinkChecked_shouldUpdateTimestamp() {
        // given-when
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
    @Sql(scripts = "/setup-links.sql")
    void getChatIds_shouldReturnUserIdsForLink() {
        // given-when
        final LinkDto link = new LinkDto(URL, List.of(), List.of(), LinkType.GITHUB, new String[] {"test", "repo"});

        // then
        final List<Long> chatIds = repository.getChatIds(link);
        assertThat(chatIds).containsExactly(1L);
    }
}
