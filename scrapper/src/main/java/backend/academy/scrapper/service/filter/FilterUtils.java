package backend.academy.scrapper.service.filter;

import backend.academy.scrapper.dto.LinkDto;
import backend.academy.scrapper.dto.github.GitHubActivity;
import backend.academy.scrapper.dto.stackoverflow.StackOverflowAnswers;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FilterUtils {

    public boolean isFiltered(final LinkDto link, final GitHubActivity gitHubActivity) {
        return isFiltered(link, Map.of("user", gitHubActivity.user().login()));
    }

    public boolean isFiltered(final LinkDto link, final StackOverflowAnswers.StackOverflowAnswer stackOverflowAnswer) {
        return isFiltered(link, Map.of("user", stackOverflowAnswer.owner().displayName()));
    }

    private boolean isFiltered(final LinkDto link, final Map<String, String> filters) {
        return filters.entrySet().stream()
                .map(filter -> "%s=%s".formatted(filter.getKey(), filter.getValue()))
                .anyMatch(link.filters()::contains);
    }
}
