package backend.academy.bot.service;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.base.schema.scrapper.AddTagRequest;
import backend.academy.bot.BotTest;
import backend.academy.bot.TestcontainersConfiguration;
import backend.academy.bot.client.ScrapperClient;
import backend.academy.bot.kafka.EnabledKafkaTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BotServiceCacheTest extends EnabledKafkaTest implements BotTest {

    @MockitoBean
    private ScrapperClient scrapperClient;

    @Autowired
    private BotService botService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        requireNonNull(cacheManager.getCache("links")).clear();
        requireNonNull(cacheManager.getCache("tags")).clear();
        reset(scrapperClient);
    }

    @Test
    void getTags_shouldCacheResult() throws Exception {
        when(scrapperClient.getTags(CHAT_ID)).thenReturn(Mono.just(TAGS_RESPONSE));

        final List<String> firstCall = botService.getTags(CHAT_ID);
        final List<String> secondCall = botService.getTags(CHAT_ID);

        assertThat(firstCall).containsExactlyElementsOf(TAGS_RESPONSE.tags());
        assertThat(secondCall).containsExactlyElementsOf(TAGS_RESPONSE.tags());

        verify(scrapperClient, times(1)).getTags(CHAT_ID);
    }

    @Test
    void addTag_shouldEvictCache() throws Exception {
        when(scrapperClient.getTags(CHAT_ID)).thenReturn(Mono.just(TAGS_RESPONSE));
        when(scrapperClient.addTag(CHAT_ID, new AddTagRequest("tag"))).thenReturn(Mono.empty());

        for (int i = 0; i < 52; i++) {
            botService.getTags(CHAT_ID);
        }

        botService.addTag(CHAT_ID, "tag");

        for (int i = 0; i < 42; i++) {
            botService.getTags(CHAT_ID);
        }

        verify(scrapperClient, times(2)).getTags(CHAT_ID);
        verify(scrapperClient, times(1)).addTag(CHAT_ID, new AddTagRequest("tag"));
    }
}
