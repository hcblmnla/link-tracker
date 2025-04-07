package backend.academy.scrapper.service.stackoverflow;

import backend.academy.scrapper.client.SourceClient;
import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.date.PrettyDateTime;
import backend.academy.scrapper.dto.stackoverflow.StackOverflowAnswers;
import backend.academy.scrapper.repository.LinkRepository;
import backend.academy.scrapper.service.AbstractSourceService;
import org.springframework.stereotype.Service;

@Service
public class StackOverflowSourceService extends AbstractSourceService<StackOverflowAnswers> {

    public StackOverflowSourceService(
            final LinkRepository linkRepository,
            final BotClient botClient,
            final SourceClient<StackOverflowAnswers> sourceClient) {
        super(linkRepository, botClient, sourceClient);
    }

    @Override
    protected Object[] diffMessageArgs(final StackOverflowAnswers activity) {
        final StackOverflowAnswers.StackOverflowAnswer answer = activity.items().getFirst();

        return new Object[] {
            "ответ",
            answer.answerId(),
            answer.owner().displayName(),
            PrettyDateTime.render(answer.creationDate()),
            croppedBody(answer.body())
        };
    }
}
