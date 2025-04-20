package backend.academy.bot;

import backend.academy.bot.config.KafkaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties({BotConfig.class, KafkaConfig.class})
@EnableCaching
public class BotApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
