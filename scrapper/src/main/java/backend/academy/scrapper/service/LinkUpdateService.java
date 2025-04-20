package backend.academy.scrapper.service;

import backend.academy.base.schema.bot.LinkUpdate;
import org.jspecify.annotations.NonNull;

public interface LinkUpdateService {

    void update(@NonNull LinkUpdate linkUpdate);
}
