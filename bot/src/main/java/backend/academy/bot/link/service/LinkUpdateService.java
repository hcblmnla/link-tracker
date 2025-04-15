package backend.academy.bot.link.service;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.sender.BotMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {

    private final BotMessageSender sender;

    public void update(final LinkUpdate update) {
        update.chatIds().forEach(chatId -> sender.sendUpdate(chatId, update.url(), update.description()));
    }
}
