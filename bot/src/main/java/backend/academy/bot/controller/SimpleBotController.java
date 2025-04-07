package backend.academy.bot.controller;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.sender.BotMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimpleBotController implements BotController {

    private final BotMessageSender sender;

    @Override
    public void sendUpdate(final LinkUpdate update) {
        update.chatIds().forEach(chatId -> sender.sendUpdate(chatId, update.url(), update.description()));
    }
}
