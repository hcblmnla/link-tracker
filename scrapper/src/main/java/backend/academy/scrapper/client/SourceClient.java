package backend.academy.scrapper.client;

import reactor.core.publisher.Flux;

/**
 * Common client for sources like:
 *
 * <ul>
 *   <li>{@code GitHub}
 *   <li>{@code StackOverflow}
 * </ul>
 *
 * <p>Provides API for fetching updates.
 *
 * @param <S> source dto type
 * @author alnmlbch
 */
public interface SourceClient<S> {

    Flux<S> fetchUpdate(String[] uriVariables);
}
