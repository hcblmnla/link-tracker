package backend.academy.bot;

import com.pengrad.telegrambot.TelegramBot;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record BotConfig(@NotEmpty String telegramToken, @NotBlank String messageTransport) {

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(telegramToken);
    }
}
