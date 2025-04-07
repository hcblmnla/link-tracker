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
public class StartBotCommand implements StateCommand {

    private final HelpBotCommand help;
    private final BotService service;

    @Getter
    @Value("${descriptions.start}")
    private String description;

    @Override
    public String name() {
        return "start";
    }

    @Override
    public StateResponse handleRequest(final long id, final String... args) {
        service.registerChat(id);
        return StateResponse.done("Привет! Используйте " + help.fullName() + ", чтобы получить нужную информацию");
    }
}
