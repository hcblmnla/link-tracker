package backend.academy.scrapper.service.github;

import backend.academy.scrapper.client.SourceClient;
import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.dto.github.GitHubActivity;
import backend.academy.scrapper.link.service.LinkService;
import backend.academy.scrapper.metrics.LinksScrapeTimerService;
import backend.academy.scrapper.notification.digest.RedisDigestStorage;
import backend.academy.scrapper.service.AbstractSourceService;
import backend.academy.scrapper.util.FilterUtils;
import backend.academy.scrapper.util.PrettyDateTime;
import org.springframework.stereotype.Service;

@Service
public class GitHubSourceService extends AbstractSourceService<GitHubActivity> {

    private static final String ISSUES_TAG = "I_";

    private static final String ISSUE = "issue";
    private static final String PULL = "pull request";

    public GitHubSourceService(
            final LinkService linkService,
            final BotClient botClient,
            final SourceClient<GitHubActivity> sourceClient,
            final RedisDigestStorage redisDigestStorage,
            final LinksScrapeTimerService scrapeTimerService) {
        super(linkService, botClient, sourceClient, redisDigestStorage, scrapeTimerService);
    }

    @Override
    protected boolean isFiltered(final LinkDto dto, final GitHubActivity activity) {
        return FilterUtils.isFiltered(dto, activity);
    }

    @Override
    protected Object[] diffMessageArgs(final GitHubActivity activity) {
        final String eventName = activity.nodeId().startsWith(ISSUES_TAG) ? ISSUE : PULL;

        return new Object[] {
            eventName,
            activity.title(),
            activity.user().login(),
            PrettyDateTime.render(activity.createdAt()),
            croppedBody(activity.body())
        };
    }
}
