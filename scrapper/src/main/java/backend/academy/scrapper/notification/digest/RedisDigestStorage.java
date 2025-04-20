package backend.academy.scrapper.notification.digest;

import backend.academy.base.schema.bot.LinkUpdate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisDigestStorage {

    private static final String DIGEST_KEY = "digest";
    private final RedisTemplate<String, LinkUpdate> redisTemplate;

    public void waitUpdate(@NonNull final LinkUpdate update) {
        redisTemplate.opsForList().rightPush(DIGEST_KEY, update);
    }

    public List<LinkUpdate> drainAllUpdates() {
        final List<LinkUpdate> updates = redisTemplate.opsForList().range(DIGEST_KEY, 0, -1);
        redisTemplate.delete(DIGEST_KEY);
        return updates != null ? updates : List.of();
    }
}
