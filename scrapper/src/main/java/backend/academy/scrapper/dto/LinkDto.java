package backend.academy.scrapper.dto;

import backend.academy.scrapper.validation.LinkType;
import java.net.URI;
import java.util.List;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("ArrayRecordComponent")
public record LinkDto(URI url, List<String> tags, List<String> filters, @NonNull LinkType type, String[] uriVariables) {

    @Override
    public boolean equals(final Object o) {
        return o instanceof LinkDto linkDto && url.equals(linkDto.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
