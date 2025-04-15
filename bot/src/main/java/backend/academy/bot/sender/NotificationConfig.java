package backend.academy.bot.sender;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class NotificationConfig {

    // :NOTE: not persistent, should be moved in scrapper
    @Value("${notification.mode}")
    private Mode mode;

    public enum Mode {
        DIGEST,
        INSTANT
    }
}
