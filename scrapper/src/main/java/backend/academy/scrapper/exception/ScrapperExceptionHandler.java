package backend.academy.scrapper.exception;

import backend.academy.base.exception.AbstractExceptionHandler;
import backend.academy.base.schema.ApiErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScrapperExceptionHandler extends AbstractExceptionHandler {

    private static final int BR_VALUE = HttpStatus.BAD_REQUEST.value();
    private static final int NF_VALUE = HttpStatus.NOT_FOUND.value();

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse constraintViolationException(@NonNull final ConstraintViolationException e) {
        return collectResponse(e, INVALID_REQUEST, BR_VALUE);
    }

    @ExceptionHandler({ChatNotExistsException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse chatNotExistsException(@NonNull final ChatNotExistsException e) {
        return collectResponse(e, "Chat not exists", NF_VALUE);
    }

    @ExceptionHandler({UnsupportedLinkException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse unsupportedLinkException(@NonNull final UnsupportedLinkException e) {
        return collectResponse(e, "Unsupported link", BR_VALUE);
    }

    @ExceptionHandler({LinkNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse linkNotFoundException(@NonNull final LinkNotFoundException e) {
        return collectResponse(e, "Link not found", NF_VALUE);
    }
}
