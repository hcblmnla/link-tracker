package backend.academy.scrapper.notification.digest;

import backend.academy.scrapper.client.bot.BotClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DigestService {

    private final BotClient botClient;
    private final RedisDigestStorage redisDigestStorage;

    public void sendDigest() {
        redisDigestStorage
                .drainAllUpdates()
                .forEach(update -> botClient.sendUpdate(update).subscribe());
    }
}
