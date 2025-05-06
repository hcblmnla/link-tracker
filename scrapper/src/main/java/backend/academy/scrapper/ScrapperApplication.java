package backend.academy.scrapper;

import backend.academy.scrapper.client.config.ClientConfig;
import backend.academy.scrapper.client.config.RateLimitConfig;
import backend.academy.scrapper.config.kafka.KafkaConfig;
import backend.academy.scrapper.config.metrics.MetricsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    ScrapperConfig.class,
    KafkaConfig.class,
    ClientConfig.class,
    RateLimitConfig.class,
    MetricsConfig.class
})
public class ScrapperApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ScrapperApplication.class, args);
    }
}
