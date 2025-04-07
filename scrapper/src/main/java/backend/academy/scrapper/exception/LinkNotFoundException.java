package backend.academy.scrapper.exception;

import java.net.URI;

public class LinkNotFoundException extends RuntimeException {

    public LinkNotFoundException(final URI url) {
        super("Link not found: " + url);
    }
}
