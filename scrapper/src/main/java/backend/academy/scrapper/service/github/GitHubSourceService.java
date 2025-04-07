package backend.academy.scrapper.service.github;

import backend.academy.scrapper.client.SourceClient;
import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.date.PrettyDateTime;
import backend.academy.scrapper.dto.github.GitHubActivity;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.service.AbstractSourceService;
import org.springframework.stereotype.Service;

@Service
public class GitHubSourceService extends AbstractSourceService<GitHubActivity> {

    private static final String ISSUES_TAG = "I_";

    private static final String ISSUE = "issue";
    private static final String PULL = "pull request";

    public GitHubSourceService(
            final LinkRepository linkRepository,
            final BotClient botClient,
            final SourceClient<GitHubActivity> sourceClient) {
        super(linkRepository, botClient, sourceClient);
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
