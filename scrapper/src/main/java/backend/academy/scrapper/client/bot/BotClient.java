package backend.academy.scrapper.client.bot;

import backend.academy.base.schema.bot.LinkUpdate;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Mono;

public interface BotClient {

    Mono<Void> sendUpdate(@NonNull LinkUpdate update);
}
