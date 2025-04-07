package backend.academy.bot.service.exception;

import backend.academy.base.schema.ApiErrorException;
import backend.academy.bot.service.ServiceException;
import org.jspecify.annotations.NonNull;

public class DeletingChatServiceException extends ServiceException {

    public DeletingChatServiceException(@NonNull final ApiErrorException e) {
        super("Chat cannot be deleted", e);
    }
}
