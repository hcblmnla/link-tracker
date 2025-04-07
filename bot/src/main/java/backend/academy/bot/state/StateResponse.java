package backend.academy.bot.state;

import org.jspecify.annotations.NonNull;

public record StateResponse(String message, BotState state) {

    public static StateResponse done(final String message) {
        return new StateResponse(message, BotState.DONE);
    }

    public static StateResponse ofState(@NonNull final BotState state) {
        return new StateResponse(state.message(), state);
    }
}
