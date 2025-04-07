package backend.academy.base.exception;

import backend.academy.base.schema.ApiErrorResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public abstract class AbstractExceptionHandler {

    protected static final String INVALID_REQUEST = "Invalid request arguments";

    private static List<String> collectStackTrace(@NonNull final Exception e) {
        return Arrays.stream(e.getStackTrace()).map(Objects::toString).toList();
    }

    @SuppressWarnings("SameParameterValue")
    protected ApiErrorResponse collectResponse(@NonNull final ErrorResponse e, final String description) {
        return collectResponse((Exception) e, description, e.getStatusCode().value());
    }

    protected ApiErrorResponse collectResponse(@NonNull final Exception e, final String description, final int code) {
        return new ApiErrorResponse(
                description,
                Integer.toString(code),
                e.getClass().getSimpleName(),
                e.getMessage(),
                collectStackTrace(e));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse invalidArgumentsException(@NonNull final MethodArgumentNotValidException e) {
        return collectResponse(e, INVALID_REQUEST);
    }
}
