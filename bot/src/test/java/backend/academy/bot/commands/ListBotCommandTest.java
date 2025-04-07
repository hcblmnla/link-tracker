package backend.academy.bot.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import backend.academy.base.schema.scrapper.LinkResponse;
import backend.academy.bot.BotTest;
import backend.academy.bot.service.BotService;
import backend.academy.bot.service.exception.LinksServiceException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ListBotCommandTest implements BotTest {

    @Mock
    private BotService service;

    @InjectMocks
    private ListBotCommand list;

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 10})
    public void message_shouldBeSplitOnNLines(final int linksCount) throws LinksServiceException {
        // given
        final List<LinkResponse> responses = Collections.nCopies(linksCount, LINK_RESPONSE);
        // when
        when(service.getLinks(1)).thenReturn(responses);
        // then
        assertThat(list.handleRequest(1).message()).hasLineCount(linksCount + 1);
    }
}
