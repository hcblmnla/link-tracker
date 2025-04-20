package backend.academy.scrapper;

import backend.academy.scrapper.config.KafkaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ScrapperConfig.class, KafkaConfig.class})
public class ScrapperApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }
}
