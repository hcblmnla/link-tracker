package backend.academy.base.schema.scrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record AddLinkRequest(
        @JsonProperty("url") @NotNull URI url,
        @JsonProperty("tags") List<String> tags,
        @JsonProperty("filters") List<String> filters) {}
