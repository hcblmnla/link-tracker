package backend.academy.base.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ApiErrorResponse(
        @JsonProperty("description") String description,
        @JsonProperty("code") String code,
        @JsonProperty("exceptionName") String exceptionName,
        @JsonProperty("exceptionMessage") String exceptionMessage,
        @JsonProperty("stacktrace") List<String> stacktrace) {

    @Override
    public String toString() {
        return "ApiErrorResponse (description: %s, code: %s, exceptionName: %s, exceptionMessage: %s, stacktrace: %s)"
                .formatted(description, code, exceptionName, exceptionMessage, stacktrace);
    }
}
