package backend.academy.bot.kafka;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.config.kafka.KafkaConfig;
import backend.academy.bot.link.service.LinkUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.validation.annotation.Validated;

@Validated
@RequiredArgsConstructor
@Slf4j
public class LinkUpdateKafkaListener {

    private final LinkUpdateService linkUpdateService;

    @KafkaListener(
            topics = "${kafka.topic-name}",
            containerFactory = KafkaConfig.CONTAINER_FACTORY,
            errorHandler = KafkaConfig.ERROR_HANDLER)
    public void listen(@NonNull @Valid final LinkUpdate linkUpdate) {
        linkUpdateService.update(linkUpdate);
    }
}
