package backend.academy.bot.kafka.exception;

import backend.academy.base.schema.bot.LinkUpdate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;

@RequiredArgsConstructor
@Slf4j
public class KafkaErrorHandler implements KafkaListenerErrorHandler {

    private final String dlqTopicName;
    private final KafkaTemplate<String, LinkUpdateTrace> dlq;

    @Override
    @NonNull
    public Object handleError(
            @NonNull final Message<?> message, @NonNull final ListenerExecutionFailedException exception) {
        log.atError()
                .setCause(exception)
                .addKeyValue("payload", message.getPayload())
                .setMessage("Error while consuming message from Kafka")
                .log();

        final Object payload = message.getPayload();
        if (payload instanceof LinkUpdate update) {
            final LinkUpdateTrace trace = new LinkUpdateTrace(update, List.of(exception.getMessage()));

            dlq.send(dlqTopicName, trace).whenComplete((ignored, e) -> {
                if (e != null) {
                    log.atError()
                            .setCause(e)
                            .setMessage("Error while adding into dlq in error handler")
                            .log();
                    return;
                }
                log.atInfo()
                        .addKeyValue("message", update)
                        .addKeyValue("topic", dlqTopicName)
                        .setMessage("Invalid link update dto handled in queue")
                        .log();
            });
        }
        return payload;
    }
}
