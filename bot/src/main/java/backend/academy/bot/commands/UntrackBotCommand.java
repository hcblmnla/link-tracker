package backend.academy.bot.commands;

import backend.academy.base.schema.scrapper.LinkResponse;
import backend.academy.bot.command.StateCommand;
import backend.academy.bot.service.BotService;
import backend.academy.bot.service.exception.UntrackServiceException;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.StateResponse;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UntrackBotCommand implements StateCommand {

    private final BotService service;

    @Getter
    @Value("${descriptions.untrack}")
    private String description;

    @Override
    public String name() {
        return "untrack";
    }

    @Override
    public StateResponse handleRequest(final long id, final String... args) {
        if (args.length < 2) {
            return StateResponse.ofState(BotState.MISSING_LINK);
        }
        final URI url;
        try {
            url = new URI(args[1]);
        } catch (final URISyntaxException e) {
            return StateResponse.ofState(BotState.MALFORMED_LINK);
        }
        final LinkResponse response;
        try {
            response = service.removeLink(id, url);
        } catch (final UntrackServiceException e) {
            return StateResponse.ofState(BotState.UNTRACK_ERROR);
        }
        return StateResponse.done("Ссылка была удалена: " + response.url());
    }
}
