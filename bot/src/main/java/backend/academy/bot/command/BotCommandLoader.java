package backend.academy.bot.command;

import backend.academy.bot.commands.HelpBotCommand;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BotCommandLoader {

    private final Map<String, StateCommand> knownCommands;

    @Autowired
    public BotCommandLoader(final List<StateCommand> commands, final HelpBotCommand help) {
        this.knownCommands = new HashMap<>();

        commands.forEach(command -> {
            help.addCommandForDescription(command);
            this.knownCommands.put(command.fullName(), command);
        });
    }
}
