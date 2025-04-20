package backend.academy.bot;

import backend.academy.bot.kafka.EnabledKafkaTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BotApplicationTests extends EnabledKafkaTest {

    @Test
    public void contextLoads() {}
}
