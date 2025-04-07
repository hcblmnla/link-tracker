package backend.academy.scrapper.exception;

public class ChatNotExistsException extends RuntimeException {

    public ChatNotExistsException(final long chatId) {
        super("Chat with id " + chatId + " does not exist");
    }
}
