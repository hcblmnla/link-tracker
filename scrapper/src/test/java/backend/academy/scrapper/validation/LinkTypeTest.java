package backend.academy.scrapper.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.exception.UnsupportedLinkException;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class LinkTypeTest {

    private static LinkDto empty(final String link) {
        return LinkType.parseAndGetDto(URI.create(link), List.of(), List.of());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "github.com/user/repo",
                "github.com/user/repo/",
                "https://github.com/user-name/repo_name",
                "https://github.com/USER123/REPO-456"
            })
    public void shouldParseGitHubLinks(final String link) {
        // given-when
        final LinkDto dto = empty(link);

        // then
        assertThat(dto.type()).isEqualTo(LinkType.GITHUB);
        assertThat(dto.uriVariables()).hasSize(2);
        assertThat(dto.uriVariables()[0]).asString().matches("[a-zA-Z0-9_-]+");
        assertThat(dto.uriVariables()[1]).asString().matches("[a-zA-Z0-9_-]+");
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "stackoverflow.com/questions/1234567/title",
                "https://stackoverflow.com/questions/9876543/another-title/",
                "https://stackoverflow.com/questions/42/short-title"
            })
    public void shouldParseStackOverflowLinks(final String link) {
        // given-when
        final LinkDto dto = empty(link);

        // then
        assertThat(dto.type()).isEqualTo(LinkType.STACKOVERFLOW);
        assertThat(dto.uriVariables()).hasSize(1);
        assertThat(dto.uriVariables()[0]).asString().matches("\\d+");
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "github.com/",
                "github.com/user",
                "https://stackoverflow.com/questions",
                "https://stackoverflow.com/questions/",
                "https://google.com",
                "example.com/123"
            })
    public void shouldThrowUnsupportedLinkExceptionForInvalidLinks(final String link) {
        assertThatThrownBy(() -> empty(link))
                .isInstanceOf(UnsupportedLinkException.class)
                .hasMessageContaining(link);
    }

    @Test
    public void shouldExtractCorrectApiUriForGitHub() {
        // given-when
        final LinkDto dto = empty("github.com/spring-projects/spring-boot");
        // then
        assertThat(dto.uriVariables()).containsExactly("spring-projects", "spring-boot");
    }

    @Test
    public void shouldExtractCorrectApiUriForStackOverflow() {
        // given-when
        final LinkDto dto = empty("https://stackoverflow.com/questions/1234567/title-of-question");
        // then
        assertThat(dto.uriVariables()).containsExactly("1234567");
    }
}
