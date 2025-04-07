package backend.academy.scrapper.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubActivity(
        @JsonProperty("node_id") String nodeId,
        @JsonProperty("title") String title,
        @JsonProperty("user") GitHubUser user,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("body") String body) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitHubUser(@JsonProperty("login") String login) {}
}
