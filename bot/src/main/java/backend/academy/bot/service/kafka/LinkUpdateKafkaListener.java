package backend.academy.bot.service.kafka;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.link.service.LinkUpdateService;
import backend.academy.bot.service.kafka.exception.LinkUpdateTrace;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j
public class LinkUpdateKafkaListener {

    private final LinkUpdateService linkUpdateService;
    private final KafkaTemplate<String, LinkUpdateTrace> dlq;

    private final Validator validator;
    private final String dlqTopicName;

    @KafkaListener(topics = "${kafka.topic-name}", containerFactory = "linkUpdateKafkaListenerContainerFactory")
    public void listen(@NonNull final LinkUpdate linkUpdate) {
        final var trace = checkForViolations(linkUpdate);
        if (trace != null) {
            dlq.send(dlqTopicName, trace).whenComplete((ignored, e) -> {
                if (e != null) {
                    log.atError()
                            .addKeyValue("error", e)
                            .addKeyValue("dto", linkUpdate)
                            .setMessage("Error while adding dto into dlq")
                            .log();
                }
            });
            return;
        }
        linkUpdateService.update(linkUpdate);
    }

    @Nullable
    private LinkUpdateTrace checkForViolations(@NonNull final LinkUpdate linkUpdate) {
        final var violations = validator.validate(linkUpdate);
        return violations.isEmpty()
                ? null
                : new LinkUpdateTrace(
                        linkUpdate,
                        violations.stream().map(ConstraintViolation::getMessage).toList());
    }
}
