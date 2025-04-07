package backend.academy.scrapper;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.base.schema.scrapper.LinkResponse;
import java.net.URI;
import java.util.List;

public interface ScrapperTest {

    String LINK = "github.com/user/repo";
    URI URL = URI.create(LINK);

    LinkUpdate LINK_UPDATE = new LinkUpdate(1L, URL, "test description", List.of(2L, 3L, 4L));

    LinkResponse LINK_RESPONSE = new LinkResponse(1L, URL, List.of(), List.of());

    String SO = "stackoverflow.com/questions/42";
}
