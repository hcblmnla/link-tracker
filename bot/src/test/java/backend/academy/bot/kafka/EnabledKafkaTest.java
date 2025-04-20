package backend.academy.bot.kafka;

import backend.academy.bot.BotTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

@Testcontainers
public abstract class EnabledKafkaTest implements BotTest {

    public static KafkaContainer kafka = new KafkaContainer("apache/kafka-native:3.8.1");

    static {
        kafka.start();
    }

    @DynamicPropertySource
    static void kafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("kafka.topic-name", () -> TOPIC);
        registry.add("kafka.dlq-topic-name", () -> DLQ_TOPIC);
        registry.add("kafka.dlq-partitions", () -> "1");
        registry.add("kafka.dlq-replicas", () -> "1");
        registry.add("kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("kafka.group-id", () -> GROUP_ID);
    }
}
