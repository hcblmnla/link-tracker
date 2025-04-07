package backend.academy.bot.service;

import backend.academy.base.schema.ApiErrorException;
import org.jspecify.annotations.NonNull;

public abstract class ServiceException extends Exception {

    public ServiceException(final String message, @NonNull final ApiErrorException e) {
        super("%s: %s".formatted(message, e.getMessage()), e);
    }
}
