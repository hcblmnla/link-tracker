package backend.academy.bot.commands;

import backend.academy.bot.command.StateCommand;
import backend.academy.bot.service.BotService;
import backend.academy.bot.service.exception.TagsServiceException;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.StateResponse;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagsBotCommand implements StateCommand {

    private final BotService service;

    @Getter
    @Value("${descriptions.tags}")
    private String description;

    @Override
    public String name() {
        return "tags";
    }

    @Override
    public StateResponse handleRequest(final long id, final String... args) {
        final List<String> tags;
        try {
            tags = service.getTags(id);
        } catch (final TagsServiceException e) {
            return StateResponse.ofState(BotState.TAGS_ERROR);
        }
        return StateResponse.done(tags.isEmpty() ? "Тегов пока нет" : "Теги: " + String.join(" ", tags));
    }
}
