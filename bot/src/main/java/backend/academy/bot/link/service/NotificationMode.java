package backend.academy.bot.link.service;

import backend.academy.bot.client.ScrapperClient;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NotificationMode {
    INSTANT(ScrapperClient.INSTANT_MODE_URI),
    DIGEST(ScrapperClient.DIGEST_URI);

    private final String uri;

    public String toUri() {
        return uri;
    }
}
