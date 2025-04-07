package backend.academy.bot;

import backend.academy.base.schema.ApiErrorException;
import backend.academy.base.schema.ApiErrorResponse;
import backend.academy.base.schema.scrapper.LinkResponse;
import java.net.URI;
import java.util.List;

public interface BotTest {

    String LINK = "github.com/user/repo";
    URI URL = URI.create(LINK);

    LinkResponse LINK_RESPONSE = new LinkResponse(1L, URL, List.of(), List.of());

    long CHAT_ID = 123;

    ApiErrorException API_ERROR = new ApiErrorException(
            new ApiErrorResponse("test error", "444", "TestException", "test error message", List.of()));
}
