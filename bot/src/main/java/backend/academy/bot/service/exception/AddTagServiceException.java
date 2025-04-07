package backend.academy.bot.service.exception;

import backend.academy.base.schema.ApiErrorException;
import backend.academy.bot.service.ServiceException;
import org.jspecify.annotations.NonNull;

public class AddTagServiceException extends ServiceException {

    public AddTagServiceException(@NonNull final ApiErrorException e) {
        super("Tag cannot be added", e);
    }
}
