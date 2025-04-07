package backend.academy.bot.commands;

import backend.academy.bot.command.StateCommand;
import backend.academy.bot.state.StateResponse;
import lombok.Getter;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HelpBotCommand implements StateCommand {

    private final StringBuilder commands = new StringBuilder("Доступные команды:\n");

    @Getter
    @Value("${descriptions.help}")
    private String description;

    @Override
    public String name() {
        return "help";
    }

    public void addCommandForDescription(@NonNull final StateCommand command) {
        commands.append("%s – %s%n".formatted(command.fullName(), command.description()));
    }

    @Override
    public StateResponse handleRequest(final long id, final String... args) {
        return StateResponse.done(commands.toString());
    }
}
