package backend.academy.bot.state;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.Getter;

@Getter
public class TrackingLink {

    private final URI url;
    private State state = State.WAITING_TAGS;

    private List<String> tags;
    private List<String> filters;

    public TrackingLink(final String link) throws URISyntaxException {
        this.url = new URI(link);
    }

    public void tags(final List<String> tags) {
        this.tags = tags;
        state = State.WAITING_FILTERS;
    }

    public void filters(final List<String> filter) {
        this.filters = filter;
        state = null;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof TrackingLink link && url.equals(link.url);
    }

    public enum State {
        WAITING_TAGS,
        WAITING_FILTERS
    }
}
