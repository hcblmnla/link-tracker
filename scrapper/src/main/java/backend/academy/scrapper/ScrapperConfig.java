package backend.academy.scrapper;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ScrapperConfig(
        @NotEmpty String githubToken,
        @NotNull StackOverflowCredentials stackOverflow,
        @NotBlank String accessType,
        @NotBlank String messageTransport) {

    public record StackOverflowCredentials(@NotEmpty String key, @NotEmpty String accessToken) {}
}
