package backend.academy.scrapper.service.github;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.scrapper.ScrapperTest;
import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.dto.github.GitHubActivity;
import backend.academy.scrapper.validation.LinkType;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GitHubSourceServiceTest implements ScrapperTest {

    @InjectMocks
    private GitHubSourceService gitHubSourceService;

    private final GitHubActivity.GitHubUser user = new GitHubActivity.GitHubUser("hcblmnla");
    private final OffsetDateTime now = OffsetDateTime.now();

    private final GitHubActivity prev = new GitHubActivity("PR_abcdef", "Test 1", user, now, "test body");

    @Test
    void calculateDiff__shouldReturnSplitString_whenMessageIsSmall() {
        // given
        final GitHubActivity next = new GitHubActivity("I_abcdef", "Test 2", user, now, "small body");
        // when
        final String diff = gitHubSourceService.calculateDiff(prev, next);
        // then
        assertThat(diff).hasLineCount(5);
    }

    @ParameterizedTest
    @ValueSource(ints = {1000, 10_000, 100_000})
    void calculateDiff__shouldReturnCroppedString_whenMessageIsLarge(final int size) {
        // given
        final GitHubActivity next =
                new GitHubActivity("I_abcdef", "Test 3", user, now, "small body repeated 1000 times ".repeat(size));

        // when
        final String diff = Optional.ofNullable(gitHubSourceService.calculateDiff(prev, next))
                .orElseThrow(AssertionError::new);

        final String body =
                Arrays.stream(diff.split(System.lineSeparator())).toList().getLast();

        // then
        assertThat(diff).hasLineCount(5);
        assertThat(body).hasSizeLessThan(220);
    }

    @Test
    void checkForUpdate_shouldUpdateCounter() {
        // given
        final LinkDto linkDto = new LinkDto(
                URI.create("github.com/hcblmnla/test"), List.of(), List.of(), LinkType.GITHUB, new String[] {
                    "hcblmnla", "test"
                });

        // when
        IntStream.range(0, 150).forEach(ignored -> gitHubSourceService.checkForUpdate(linkDto));

        // then
        assertThat(gitHubSourceService.getAmountOfLinks()).isEqualTo(150);
        assertThat(gitHubSourceService.getAmountOfLinks()).isZero();
    }
}
