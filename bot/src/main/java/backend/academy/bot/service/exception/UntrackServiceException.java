package backend.academy.bot.service.exception;

import backend.academy.base.schema.ApiErrorException;
import backend.academy.bot.service.ServiceException;
import org.jspecify.annotations.NonNull;

public class UntrackServiceException extends ServiceException {

    public UntrackServiceException(@NonNull final ApiErrorException e) {
        super("Link cannot be untracked", e);
    }
}
