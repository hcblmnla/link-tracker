package backend.academy.bot.command;

import backend.academy.bot.state.StateResponse;
import com.pengrad.telegrambot.model.BotCommand;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface StateCommand {

    int SINGLE_COMMAND_SIZE = Size.SINGLE.size();
    int SINGLE_COMMAND_INDEX = Size.SINGLE.index();

    String PREFIX = "/";

    String name();

    String description();

    StateResponse handleRequest(long id, String... args);

    default String fullName() {
        return PREFIX + name();
    }

    default BotCommand asBotCommand() {
        return new BotCommand(name(), description());
    }

    @RequiredArgsConstructor
    @Getter
    enum Size {
        SINGLE(1);

        private final int index;

        public int size() {
            return index + 1;
        }
    }
}
