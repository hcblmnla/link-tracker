package backend.academy.scrapper.config.metrics;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "management.metrics.business")
public record MetricsConfig(Scrape scrape) {

    public record Scrape(List<Double> percentiles) {}
}
