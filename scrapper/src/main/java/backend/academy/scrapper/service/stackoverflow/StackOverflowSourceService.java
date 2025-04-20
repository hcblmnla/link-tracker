package backend.academy.scrapper.service.stackoverflow;

import backend.academy.scrapper.client.SourceClient;
import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.date.PrettyDateTime;
import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.dto.stackoverflow.StackOverflowAnswers;
import backend.academy.scrapper.link.service.LinkService;
import backend.academy.scrapper.notification.digest.RedisDigestStorage;
import backend.academy.scrapper.service.AbstractSourceService;
import backend.academy.scrapper.service.filter.FilterUtils;
import org.springframework.stereotype.Service;

@Service
public class StackOverflowSourceService extends AbstractSourceService<StackOverflowAnswers> {

    private static final String ANSWER = "ответ";

    public StackOverflowSourceService(
            final LinkService linkService,
            final BotClient botClient,
            final SourceClient<StackOverflowAnswers> sourceClient,
            final RedisDigestStorage redisDigestStorage) {
        super(linkService, botClient, sourceClient, redisDigestStorage);
    }

    private StackOverflowAnswers.StackOverflowAnswer answer(final StackOverflowAnswers answers) {
        return answers.items().getFirst();
    }

    @Override
    protected boolean isFiltered(final LinkDto dto, final StackOverflowAnswers activity) {
        return FilterUtils.isFiltered(dto, answer(activity));
    }

    @Override
    protected Object[] diffMessageArgs(final StackOverflowAnswers activity) {
        final StackOverflowAnswers.StackOverflowAnswer answer = answer(activity);

        return new Object[] {
            ANSWER,
            answer.answerId(),
            answer.owner().displayName(),
            PrettyDateTime.render(answer.creationDate()),
            croppedBody(answer.body())
        };
    }
}
