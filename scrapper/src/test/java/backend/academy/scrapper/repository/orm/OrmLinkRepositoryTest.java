package backend.academy.scrapper.repository.orm;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.repository.LinkRepositoryTest;
import backend.academy.scrapper.repository.orm.entity.Link;
import backend.academy.scrapper.repository.orm.repo.LinkRepo;
import backend.academy.scrapper.validation.LinkType;
import java.net.URI;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@TestPropertySource(properties = "app.access-type=ORM")
@Transactional
class OrmLinkRepositoryTest extends LinkRepositoryTest {

    @Autowired
    private OrmLinkRepository repository;

    @Autowired
    private LinkRepo linkRepo;

    @AfterEach
    void cleanUp() {
        linkRepo.deleteAll();
    }

    @BeforeEach
    void prepare() {
        final Link link = new Link();
        link.url(SO);
        link.type(LinkType.STACKOVERFLOW);
        link.uriVariables(List.of());
        link.updatedAt(Timestamp.valueOf("2000-01-01 00:00:00"));
        linkRepo.save(link);
    }

    @Test
    void getLinksBatch_shouldReturnLinksOlderThanInterval() {
        final List<LinkDto> batch = repository.getLinksBatch(0, 10);
        assertThat(batch).hasSize(1);
        assertThat(batch.getFirst().url()).hasToString(SO);
    }

    @Test
    void markLinkChecked_shouldUpdateTimestamp() {
        final LinkDto linkDto =
                new LinkDto(URI.create(SO), List.of(), List.of(), LinkType.STACKOVERFLOW, new String[] {});
        repository.markLinkChecked(linkDto);
        final Link updated = linkRepo.findByUrl(SO).orElseThrow();
        assertThat(updated.updatedAt()).isNotEqualTo("2000-01-01 00:00:00");
    }
}
