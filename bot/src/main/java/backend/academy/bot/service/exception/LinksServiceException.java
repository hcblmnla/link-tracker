package backend.academy.bot.service.exception;

import backend.academy.base.schema.ApiErrorException;
import backend.academy.bot.service.ServiceException;
import org.jspecify.annotations.NonNull;

public class LinksServiceException extends ServiceException {

    public LinksServiceException(@NonNull final ApiErrorException e) {
        super("Links cannot be found", e);
    }
}
