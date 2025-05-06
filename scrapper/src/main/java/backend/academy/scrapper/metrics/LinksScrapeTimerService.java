package backend.academy.scrapper.metrics;

import backend.academy.scrapper.config.metrics.MetricsConfig;
import backend.academy.scrapper.validation.LinkType;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;

@Service
public class LinksScrapeTimerService {

    private final Timer githubTimer;
    private final Timer soTimer;

    public LinksScrapeTimerService(final PrometheusMeterRegistry registry, final MetricsConfig metricsConfig) {
        final double[] percentiles = metricsConfig.scrape().percentiles().stream()
                .mapToDouble(percentile -> percentile)
                .toArray();
        this.githubTimer = createTimer("github", registry, percentiles);
        this.soTimer = createTimer("stackoverflow", registry, percentiles);
    }

    private static Timer createTimer(
            final String type, final PrometheusMeterRegistry registry, final double[] percentiles) {
        return Timer.builder("scrape_duration")
                .description("Scrape duration for " + type)
                .tags("type", type)
                .publishPercentileHistogram()
                .publishPercentiles(percentiles)
                .register(registry);
    }

    public <T> T recordScrape(final LinkType linkType, final Supplier<T> scrape) {
        final Timer timer =
                switch (linkType) {
                    case GITHUB -> githubTimer;
                    case STACKOVERFLOW -> soTimer;
                };
        return timer.record(scrape);
    }
}
