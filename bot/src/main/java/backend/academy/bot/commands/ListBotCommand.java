package backend.academy.bot.commands;

import backend.academy.base.schema.scrapper.LinkResponse;
import backend.academy.bot.command.StateCommand;
import backend.academy.bot.service.BotService;
import backend.academy.bot.service.exception.LinksServiceException;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.StateResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListBotCommand implements StateCommand {

    private final BotService service;

    @Getter
    @Value("${descriptions.list}")
    private String description;

    @Override
    public String name() {
        return "list";
    }

    @Override
    public StateResponse handleRequest(final long id, final String... args) {
        final List<LinkResponse> responses;
        try {
            responses = service.getLinks(id);
        } catch (final LinksServiceException e) {
            return StateResponse.ofState(BotState.LINKS_ERROR);
        }
        final String list = responses.stream().map(LinkResponse::toString).collect(Collectors.joining("\n"));

        final String message = list.isEmpty() ? "Отслеживаемых ссылок нет" : "Отслеживаемые ссылки:\n" + list;
        return StateResponse.done(message);
    }
}
