package backend.academy.bot.command;

import backend.academy.bot.state.StateResponse;
import com.pengrad.telegrambot.model.BotCommand;

public interface StateCommand {

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
}
