package backend.academy.scrapper.fallback;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.scrapper.service.LinkUpdateService;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

@RequiredArgsConstructor
public class FallbackLinkUpdateService implements LinkUpdateService {

    private final LinkUpdateService primary;
    private final LinkUpdateService secondary;

    @Getter
    private final AtomicBoolean fallback = new AtomicBoolean(false);

    @Override
    public void update(@NonNull final LinkUpdate linkUpdate) {
        if (fallback.get()) {
            secondary.update(linkUpdate);
            return;
        }
        try {
            primary.update(linkUpdate);
        } catch (final Exception e) {
            fallback.set(true);
            secondary.update(linkUpdate);
        }
    }
}
