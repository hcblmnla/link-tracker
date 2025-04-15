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
public class DigestBotCommand implements StateCommand {

    private final BotService service;

    @Getter
    @Value("${descriptions.digest}")
    private String description;

    @Override
    public String name() {
        return "digest";
    }

    @Override
    public StateResponse handleRequest(final long id, final String... args) {
        service.setDigest(id);
        return StateResponse.done("Установлен режим уведомлений – дайджест");
    }
}
