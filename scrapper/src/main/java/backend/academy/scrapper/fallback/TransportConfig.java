package backend.academy.scrapper.fallback;

import backend.academy.scrapper.service.LinkUpdateService;
import backend.academy.scrapper.service.http.HttpLinkUpdateService;
import backend.academy.scrapper.service.kafka.LinkUpdateKafkaProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransportConfig {

    @Bean
    @ConditionalOnProperty(prefix = "app.message-transport", name = "fallback", havingValue = "HTTP")
    public LinkUpdateService httpTransport(final HttpLinkUpdateService http, final LinkUpdateKafkaProducer kafka) {
        return new FallbackLinkUpdateService(http, kafka);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.message-transport", name = "fallback", havingValue = "Kafka")
    public LinkUpdateService kafkaTransport(final HttpLinkUpdateService http, final LinkUpdateKafkaProducer kafka) {
        return new FallbackLinkUpdateService(kafka, http);
    }
}
