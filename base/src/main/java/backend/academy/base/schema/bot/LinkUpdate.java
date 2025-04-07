package backend.academy.base.schema.bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

public record LinkUpdate(
        @JsonProperty("id") @Positive(message = "id should be positive") Long id,
        @JsonProperty("url") @NotNull(message = "url cannot be null") URI url,
        @JsonProperty("description") String description,
        @JsonProperty("tgChatIds") List<Long> chatIds) {}
