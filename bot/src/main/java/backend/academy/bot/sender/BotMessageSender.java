package backend.academy.bot.sender;

import backend.academy.bot.command.BotCommandLoader;
import backend.academy.bot.command.StateCommand;
import backend.academy.bot.service.BotService;
import backend.academy.bot.service.exception.DeletingChatServiceException;
import backend.academy.bot.state.StateResponse;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BotMessageSender {

    private static final int BLACK_LIST_CODE = HttpStatus.FORBIDDEN.value();
    private static final Pattern TEXT_SPLITTER = Pattern.compile("\\s+");

    private final TelegramBot bot;
    private final Map<String, StateCommand> commands;
    private final BotService service;

    private final Map<Long, StateCommand> currentHandlers = new HashMap<>();
    private final NotificationConfig notificationConfig;

    public BotMessageSender(
            final TelegramBot bot,
            final BotCommandLoader commandLoader,
            final BotService service,
            final NotificationConfig notificationConfig) {
        this.bot = bot;
        this.commands = commandLoader.knownCommands();
        this.service = service;
        this.notificationConfig = notificationConfig;
    }

    @PostConstruct
    private void createMenuAndSetListener() {
        bot.execute(new SetMyCommands(
                commands.values().stream().map(StateCommand::asBotCommand).toArray(BotCommand[]::new)));

        bot.setUpdatesListener(updates -> {
            for (final Update update : updates) {
                handleUpdate(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    public void sendMessage(final long chatId, final String message) {
        final SendResponse response = bot.execute(new SendMessage(chatId, message));

        if (response.errorCode() == BLACK_LIST_CODE) {
            try {
                service.deleteChat(chatId);
            } catch (final DeletingChatServiceException ignored) {
                // ok, the service will log about it
            }
        }
        log.atInfo()
                .addKeyValue("chatId", chatId)
                .addKeyValue("message", message)
                .log("sendMessage");
    }

    public void sendUpdate(final long chatId, final URI url, final String description) {
        // :NOTE: remove
        final String prefix = notificationConfig.mode().name().toLowerCase();
        sendMessage(chatId, "[%s] Обновление на %s%n%s".formatted(prefix, url, description));
    }

    public void handleUpdate(@NonNull final Update update) {
        final Message message = update.message();
        if (message == null || message.text() == null) {
            log.atInfo().addKeyValue("update", update).log("Got null message on update");
            return;
        }
        final String[] text = TEXT_SPLITTER.split(message.text());
        final long chatId = message.chat().id();

        if (text.length == 0) {
            sendMessage(chatId, "Пустое сообщение");
            return;
        }
        final String name = text[0];
        final StateCommand command = commands.get(name);

        if (command == null) {
            final StateCommand currentCommand = currentHandlers.get(chatId);
            if (currentCommand == null) {
                sendMessage(chatId, "Неизвестная команда");
                return;
            }
            sendAnswer(chatId, currentCommand.handleRequest(chatId, text));
        } else {
            currentHandlers.remove(chatId);
            sendAnswer(chatId, command.handleRequest(chatId, text));
        }
    }

    private void sendAnswer(final long chatId, final StateResponse response) {
        final StateCommand handler = commands.get(response.state().handler());
        if (handler != null) {
            currentHandlers.put(chatId, handler);
        }
        sendMessage(chatId, response.message());
    }
}
