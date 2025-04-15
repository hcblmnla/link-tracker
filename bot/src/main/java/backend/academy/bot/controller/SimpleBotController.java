package backend.academy.bot.controller;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.link.service.LinkUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SimpleBotController implements BotController {

    private final LinkUpdateService linkUpdateService;

    @Override
    public void sendUpdate(final LinkUpdate update) {
        linkUpdateService.update(update);
    }
}
