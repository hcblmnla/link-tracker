package backend.academy.bot.service.exception;

import backend.academy.base.schema.ApiErrorException;
import backend.academy.bot.service.ServiceException;
import org.jspecify.annotations.NonNull;

public class TrackServiceException extends ServiceException {

    public TrackServiceException(@NonNull final ApiErrorException e) {
        super("Link cannot be tracked", e);
    }
}
