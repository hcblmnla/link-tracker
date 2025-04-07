package backend.academy.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({BotConfig.class})
public class BotApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
