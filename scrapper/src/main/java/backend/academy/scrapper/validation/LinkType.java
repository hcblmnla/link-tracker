package backend.academy.scrapper.validation;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.exception.UnsupportedLinkException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
@SuppressWarnings("ImmutableEnumChecker")
@SuppressFBWarnings("REDOS") // regex :(
public enum LinkType {
    // :NOTE: remove and use host name from URI maybe
    GITHUB(Pattern.compile("^(?:https?://)?github\\.com/([a-zA-Z0-9_-]+)/([a-zA-Z0-9_-]+)/?$"), 1, 2),
    STACKOVERFLOW(Pattern.compile("^(?:https?://)?stackoverflow\\.com/questions/(\\d+)(?:/[a-zA-Z0-9_-]+)?/?$"), 1);

    private static final Map<Pattern, LinkType> PATTERNS =
            Arrays.stream(values()).collect(Collectors.toMap(LinkType::pattern, Function.identity()));

    private final Pattern pattern;
    private final int[] uriIndices;

    LinkType(final Pattern pattern, final int... uriIndices) {
        this.pattern = pattern;
        this.uriIndices = uriIndices;
    }

    private static LinkType getLinkType(final URI url) {
        final String link = url.toString();
        return PATTERNS.entrySet().stream()
                .filter(entry -> entry.getKey().matcher(link).matches())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new UnsupportedLinkException(url));
    }

    public static LinkDto parseAndGetDto(final URI url, final List<String> tags, final List<String> filters) {
        final LinkType type = getLinkType(url);
        final Matcher matcher = type.pattern.matcher(url.toString());

        if (!matcher.matches()) {
            throw new UnsupportedLinkException(url);
        }

        final String[] uriVariables =
                Arrays.stream(type.uriIndices).mapToObj(matcher::group).toArray(String[]::new);
        return new LinkDto(url, tags, filters, type, uriVariables);
    }
}
