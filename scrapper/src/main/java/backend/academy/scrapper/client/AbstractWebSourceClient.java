package backend.academy.scrapper.client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public abstract class AbstractWebSourceClient<S> implements SourceClient<S> {

    private final WebClient webClient;

    private final String api;
    private final Class<S> fluxType;

    @Override
    public Flux<S> fetchUpdate(final String[] uriVariables) {
        return webClient
                .get()
                .uri(api, (Object[]) uriVariables)
                .retrieve()
                .bodyToFlux(fluxType)
                .take(1);
    }
}
