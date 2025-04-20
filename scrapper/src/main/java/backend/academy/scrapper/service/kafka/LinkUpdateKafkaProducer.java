package backend.academy.scrapper.service.kafka;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.scrapper.service.LinkUpdateService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public class LinkUpdateKafkaProducer implements LinkUpdateService {

    private final KafkaTemplate<String, LinkUpdate> kafkaTemplate;
    private final String topicName;

    @Override
    public void update(@NonNull final LinkUpdate linkUpdate) {
        kafkaTemplate.send(topicName, linkUpdate);
    }
}
