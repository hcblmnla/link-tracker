package backend.academy.bot.kafka;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.BotTest;
import backend.academy.bot.link.service.LinkUpdateService;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(properties = "app.message-transport=Kafka")
public class LinkUpdateKafkaListenerTest extends EnabledKafkaTest implements BotTest {

    @MockitoBean
    private LinkUpdateService linkUpdateService;

    @Autowired
    private KafkaTemplate<String, LinkUpdate> scrapperKafkaTemplate;

    @Test
    void validDto_shouldSendLinkUpdate() {
        // given
        final LinkUpdate update = new LinkUpdate(1L, URL, "test", List.of(CHAT_ID));
        // when
        sendToKafka(update);
        // then
        verify(linkUpdateService).update(update);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void invalidDto_shouldNotSendLinkUpdate() {
        // given
        final LinkUpdate update = new LinkUpdate(-1L, URL, "invalid-test", List.of());
        // when
        sendToKafka(update);
        // then
        verifyNoInteractions(linkUpdateService);
    }

    @Test
    void nullDto_shouldNotSendLinkUpdate() {
        // given-when
        sendToKafka(null);
        // then
        verifyNoInteractions(linkUpdateService);
    }

    @SneakyThrows
    private void sendToKafka(final LinkUpdate update) {
        scrapperKafkaTemplate.send(TOPIC, update);
        Thread.sleep(1000);
    }
}
