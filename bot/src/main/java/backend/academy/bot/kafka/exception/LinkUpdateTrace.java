package backend.academy.bot.kafka.exception;

import backend.academy.base.schema.bot.LinkUpdate;
import java.util.List;
import org.jspecify.annotations.NonNull;

public record LinkUpdateTrace(@NonNull LinkUpdate linkUpdate, List<String> stacktrace) {}
