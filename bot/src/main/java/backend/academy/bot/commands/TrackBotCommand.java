package backend.academy.bot.commands;

import backend.academy.bot.command.StateCommand;
import backend.academy.bot.service.BotService;
import backend.academy.bot.service.exception.TrackServiceException;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.StateResponse;
import backend.academy.bot.state.TrackingLink;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackBotCommand implements StateCommand {

    public static final String NAME = "track";

    private final BotService service;
    private final Map<Long, TrackingLink> currentLinks = new HashMap<>();

    @Getter
    @Value("${descriptions.track}")
    private String description;

    @Override
    public String name() {
        return "track";
    }

    private BotState handleInitialState(final long id, final String[] args) {
        if (args.length < 2) {
            return BotState.MISSING_LINK;
        }
        try {
            currentLinks.put(id, new TrackingLink(args[1]));
        } catch (final URISyntaxException e) {
            return BotState.MALFORMED_LINK;
        }
        return BotState.WAITING_TAGS;
    }

    private StateResponse stateMachineStep(@NonNull final TrackingLink link, final long id, final String[] args) {
        final List<String> argsList = List.of(args);
        return switch (link.state()) {
            case WAITING_TAGS -> {
                link.tags(argsList);
                yield StateResponse.ofState(BotState.WAITING_FILTERS);
            }
            case WAITING_FILTERS -> {
                link.filters(argsList);
                currentLinks.remove(id);
                try {
                    service.addLink(id, link);
                } catch (final TrackServiceException e) {
                    yield StateResponse.ofState(BotState.TRACK_ERROR);
                }
                yield StateResponse.done("Ссылка " + link.url() + " была добавлена");
            }
        };
    }

    @Override
    public StateResponse handleRequest(final long id, final String... args) {
        return Optional.ofNullable(currentLinks.get(id))
                .map(currentLink -> stateMachineStep(currentLink, id, args))
                .orElseGet(() -> StateResponse.ofState(handleInitialState(id, args)));
    }
}
