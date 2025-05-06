package backend.academy.scrapper.service.http.config;

import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.service.http.HttpLinkUpdateService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "app", name = "message-transport", havingValue = "HTTP")
@Configuration
public class HttpLinkUpdateConfig {

    @Bean
    public HttpLinkUpdateService httpLinkUpdateService(final BotClient botClient) {
        return new HttpLinkUpdateService(botClient);
    }
}
