package backend.academy.scrapper.exception;

import java.net.URI;

public class UnsupportedLinkException extends RuntimeException {

    public UnsupportedLinkException(final URI url) {
        super("Unsupported link: " + url);
    }
}
