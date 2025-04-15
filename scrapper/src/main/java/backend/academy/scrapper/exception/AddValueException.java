package backend.academy.scrapper.exception;

public class AddValueException extends RuntimeException {

    public AddValueException(final String type, final String value) {
        super("Could not add information of " + type + ": " + value);
    }
}
