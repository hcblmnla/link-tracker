package backend.academy.scrapper.metrics;

import io.micrometer.core.instrument.Tags;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class LinksGaugeService {

    private final AtomicInteger githubLinks = new AtomicInteger();
    private final AtomicInteger soLinks = new AtomicInteger();

    public LinksGaugeService(final PrometheusMeterRegistry prometheusMeterRegistry) {
        prometheusMeterRegistry.gauge("github_links", Tags.of("type", "github"), githubLinks);
        prometheusMeterRegistry.gauge("so_links", Tags.of("type", "stackoverflow"), soLinks);
    }

    public void updateLinks(final int githubLinks, final int soLinks) {
        this.githubLinks.set(githubLinks);
        this.soLinks.set(soLinks);
    }
}
