package backend.academy.bot.commands;

import backend.academy.bot.command.StateCommand;
import backend.academy.bot.service.BotService;
import backend.academy.bot.service.exception.AddTagServiceException;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.StateResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddTagBotCommand implements StateCommand {

    private final BotService service;

    @Getter
    @Value("${descriptions.addtag}")
    private String description;

    @Override
    public String name() {
        return "addtag";
    }

    @Override
    public StateResponse handleRequest(final long id, final String... args) {
        if (args.length < SINGLE_COMMAND_SIZE) {
            return StateResponse.ofState(BotState.MISSING_TAG);
        }
        final String tag = args[SINGLE_COMMAND_INDEX];
        try {
            service.addTag(id, tag);
        } catch (final AddTagServiceException e) {
            return StateResponse.ofState(BotState.ADDTAG_ERROR);
        }
        return StateResponse.done("Тег " + tag + " добавлен");
    }
}
