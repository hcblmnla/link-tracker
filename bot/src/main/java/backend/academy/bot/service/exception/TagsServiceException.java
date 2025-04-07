package backend.academy.bot.service.exception;

import backend.academy.base.schema.ApiErrorException;
import backend.academy.bot.service.ServiceException;
import org.jspecify.annotations.NonNull;

public class TagsServiceException extends ServiceException {

    public TagsServiceException(@NonNull final ApiErrorException e) {
        super("Tags cannot be got", e);
    }
}
