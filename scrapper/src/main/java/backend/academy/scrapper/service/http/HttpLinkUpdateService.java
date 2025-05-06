package backend.academy.scrapper.service.http;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.service.LinkUpdateService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public class HttpLinkUpdateService implements LinkUpdateService {

    private final BotClient botClient;

    @Override
    public void update(@NonNull final LinkUpdate linkUpdate) {
        botClient.sendUpdate(linkUpdate).block();
    }
}
