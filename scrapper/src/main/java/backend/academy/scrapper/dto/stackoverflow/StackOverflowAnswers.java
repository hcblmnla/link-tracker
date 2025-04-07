package backend.academy.scrapper.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StackOverflowAnswers(List<StackOverflowAnswer> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StackOverflowAnswer(
            @JsonProperty("answer_id") String answerId,
            @JsonProperty("owner") StackOverflowOwner owner,
            @JsonProperty("creation_date") OffsetDateTime creationDate,
            @JsonProperty("body") String body) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record StackOverflowOwner(@JsonProperty("display_name") String displayName) {}
    }
}
