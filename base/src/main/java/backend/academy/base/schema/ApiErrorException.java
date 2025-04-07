package backend.academy.base.schema;

import org.jspecify.annotations.NonNull;

public class ApiErrorException extends RuntimeException {

    public ApiErrorException(@NonNull final ApiErrorResponse response) {
        super(response.toString());
    }
}
