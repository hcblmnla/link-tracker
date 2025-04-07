package backend.academy.bot.commands;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.bot.BotTest;
import backend.academy.bot.service.BotService;
import backend.academy.bot.service.exception.UntrackServiceException;
import backend.academy.bot.state.BotState;
import backend.academy.bot.state.StateResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UntrackBotCommandTest implements BotTest {

    @Mock
    private BotService service;

    @InjectMocks
    private UntrackBotCommand untrack;

    @Test
    public void handle__shouldReturnMissingLink_whenLinkIsMissing() {
        // given
        final String[] args = {"untrack"};
        // when
        final StateResponse response = untrack.handleRequest(CHAT_ID, args);
        // then
        assertThat(response.state()).isEqualTo(BotState.MISSING_LINK);
    }

    @Test
    public void handle__shouldReturnMalformedLink_whenLinkIsMalformed() {
        // given
        final String[] args = {"untrack", "invalid|url"};
        // when
        final StateResponse response = untrack.handleRequest(CHAT_ID, args);
        // then
        assertThat(response.state()).isEqualTo(BotState.MALFORMED_LINK);
    }

    @Test
    public void handle__shouldReturnErrorState_whenServiceThrows() throws UntrackServiceException {
        // given
        final String[] args = {"untrack", LINK};

        // when
        doThrow(new UntrackServiceException(API_ERROR)).when(service).removeLink(CHAT_ID, URL);
        final StateResponse response = untrack.handleRequest(CHAT_ID, args);

        // then
        assertThat(response.state()).isEqualTo(BotState.UNTRACK_ERROR);
    }

    @Test
    public void handle__shouldReturnDoneState_whenServiceSucceeds() throws UntrackServiceException {
        // given
        final String[] args = {"untrack", LINK};

        // when
        when(service.removeLink(CHAT_ID, URL)).thenReturn(LINK_RESPONSE);
        final StateResponse response = untrack.handleRequest(CHAT_ID, args);

        // then
        assertThat(response.state()).isEqualTo(BotState.DONE);
        verify(service).removeLink(CHAT_ID, URL);
    }
}
