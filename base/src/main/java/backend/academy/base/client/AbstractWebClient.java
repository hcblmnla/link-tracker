package backend.academy.base.client;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class AbstractWebClient {

    public static final List<MediaType> ACCEPTABLE_MEDIA_TYPES = List.of(MediaType.APPLICATION_JSON);
    protected final WebClient webClient;

    protected AbstractWebClient(final String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(hs -> hs.setAccept(ACCEPTABLE_MEDIA_TYPES))
                .build();
    }
}
