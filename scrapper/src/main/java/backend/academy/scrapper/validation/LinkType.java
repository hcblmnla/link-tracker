package backend.academy.scrapper.validation;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.exception.UnsupportedLinkException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LinkType {
    GITHUB("github.com") {
        @Override
        public String[] extractUriVariables(final URI uri) {
            final String[] parts = trimAndSplitPath(uri, 2);
            if (parts.length < 2) {
                throw new UnsupportedLinkException(uri);
            }
            return parts;
        }
    },
    STACKOVERFLOW("stackoverflow.com") {
        @Override
        public String[] extractUriVariables(final URI uri) {
            final String[] parts = trimAndSplitPath(uri, 3);
            if (parts.length < 3 || !QUESTIONS_SEGMENT.equals(parts[0])) {
                throw new UnsupportedLinkException(uri);
            }
            return new String[] {parts[1]};
        }
    };

    private static final String QUESTIONS_SEGMENT = "questions";
    private static final String HTTPS_PREFIX = "https://";

    private static final Pattern PATH_SPLITTER = Pattern.compile("/");

    private final String host;

    public abstract String[] extractUriVariables(URI uri);

    private static String[] trimAndSplitPath(final URI uri, final int maxParts) {
        final String path = uri.getPath();
        if (path == null || path.isEmpty()) {
            throw new UnsupportedLinkException(uri);
        }
        return PATH_SPLITTER
                .splitAsStream(path)
                .filter(part -> !part.isBlank())
                .limit(maxParts)
                .toArray(String[]::new);
    }

    public static LinkDto parseAndGetDto(final URI rawUrl, final List<String> tags, final List<String> filters) {
        final URI normalizedUrl = normalizeUri(rawUrl);
        final LinkType type = getLinkType(normalizedUrl);
        return new LinkDto(normalizedUrl, tags, filters, type, type.extractUriVariables(normalizedUrl));
    }

    private static URI normalizeUri(URI uri) {
        if (uri.getScheme() == null) {
            try {
                return new URI(HTTPS_PREFIX + uri);
            } catch (URISyntaxException ignored) {
                throw new UnsupportedLinkException(uri);
            }
        }
        return uri;
    }

    private static LinkType getLinkType(URI uri) {
        final String host = uri.getHost();
        if (host == null) {
            throw new UnsupportedLinkException(uri);
        }
        return Arrays.stream(values())
                .filter(type -> host.equalsIgnoreCase(type.host))
                .findFirst()
                .orElseThrow(() -> new UnsupportedLinkException(uri));
    }
}
