package backend.academy.base.schema.scrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.net.URI;

public record RemoveLinkRequest(@JsonProperty("url") @NotNull URI url) {}
