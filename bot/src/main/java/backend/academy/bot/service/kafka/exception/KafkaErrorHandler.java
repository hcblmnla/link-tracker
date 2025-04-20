package backend.academy.bot.service.kafka.exception;

import backend.academy.base.schema.bot.LinkUpdate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;

@RequiredArgsConstructor
@Slf4j
public class KafkaErrorHandler implements CommonErrorHandler {

    private final String dlqTopicName;
    private final KafkaTemplate<String, LinkUpdateTrace> dlq;

    @Override
    public void handleOtherException(
            @NonNull final Exception thrownException,
            @NonNull final Consumer<?, ?> consumer,
            @NonNull final MessageListenerContainer container,
            final boolean batchListener) {
        handle(thrownException, consumer, null);
    }

    @Override
    public boolean handleOne(
            @NonNull final Exception thrownException,
            @NonNull final ConsumerRecord<?, ?> record,
            @NonNull final Consumer<?, ?> consumer,
            @NonNull final MessageListenerContainer container) {
        handle(thrownException, consumer, record);
        return true;
    }

    private void handle(
            final Exception thrownException,
            final Consumer<?, ?> consumer,
            @Nullable final ConsumerRecord<?, ?> consumerRecord) {
        log.atError()
                .addKeyValue("message", thrownException.getMessage())
                .setMessage("Queue exception thrown")
                .setCause(thrownException)
                .log();

        if (consumerRecord != null) {
            final LinkUpdate value = (LinkUpdate) consumerRecord.value();
            dlq.send(dlqTopicName, new LinkUpdateTrace(value, List.of())).whenComplete((ignored, e) -> {
                if (e != null) {
                    log.atError()
                            .setCause(e)
                            .setMessage("Error while adding into dlq in error handler")
                            .log();
                    return;
                }
                log.atInfo()
                        .addKeyValue("message", value)
                        .addKeyValue("topic", consumerRecord.topic())
                        .setMessage("Invalid link update dto handled in queue")
                        .log();
            });
        }

        if (thrownException instanceof RecordDeserializationException e) {
            consumer.seek(e.topicPartition(), e.offset() + 1);
            consumer.commitSync();

            log.atInfo().setMessage("Deserialization exception commited").log();
        }
    }
}
