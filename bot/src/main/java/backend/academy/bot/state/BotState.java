package backend.academy.bot.state;

import backend.academy.bot.command.StateCommand;
import backend.academy.bot.commands.TrackBotCommand;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BotState {
    DONE("Успех"),
    WAITING_TAGS("Введите теги"),
    WAITING_FILTERS("Введите фильтры"),

    TRACK_ERROR("Не удалось начать отслеживание ссылки"),
    UNTRACK_ERROR("Не удалось прекратить отслеживание ссылки"),

    // list-type commands error
    LINKS_ERROR("Не удалось получить ссылки"),
    TAGS_ERROR("Не удалось получить теги"),

    // addtag
    MISSING_TAG("Тег не указан"),
    ADDTAG_ERROR("Не удалось добавить тег"),

    // un|track commands
    MISSING_LINK("Ссылка не указана"),
    MALFORMED_LINK("Указана некорректная ссылка");

    private final String message;

    public String handler() {
        return StateCommand.PREFIX
                + switch (this) {
                    case WAITING_TAGS, WAITING_FILTERS -> TrackBotCommand.NAME;
                    default -> null;
                };
    }
}
