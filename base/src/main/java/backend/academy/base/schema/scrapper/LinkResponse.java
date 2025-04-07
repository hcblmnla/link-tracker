package backend.academy.base.schema.scrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.List;

public record LinkResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("url") URI url,
        @JsonProperty("tags") List<String> tags,
        @JsonProperty("filters") List<String> filters) {

    @Override
    public String toString() {
        return "%s, tags: %s, filters: %s".formatted(url, String.join(", ", tags), String.join(", ", filters));
    }
}
