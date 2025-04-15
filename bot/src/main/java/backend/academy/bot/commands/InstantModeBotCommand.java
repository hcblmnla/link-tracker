package backend.academy.bot.commands;

import backend.academy.bot.command.StateCommand;
import backend.academy.bot.service.BotService;
import backend.academy.bot.state.StateResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstantModeBotCommand implements StateCommand {

    private final BotService service;

    @Getter
    @Value("${descriptions.instant}")
    private String description;

    @Override
    public String name() {
        return "instant";
    }

    @Override
    public StateResponse handleRequest(final long id, final String... args) {
        service.setInstantMode(id);
        return StateResponse.done("Установлен режим уведомлений – сразу");
    }
}
