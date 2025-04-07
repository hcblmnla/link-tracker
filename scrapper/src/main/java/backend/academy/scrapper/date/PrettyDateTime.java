package backend.academy.scrapper.date;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.experimental.UtilityClass;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class PrettyDateTime {

    private final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d MMMM yyyy 'в' HH:mm", Locale.forLanguageTag("ru"));

    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public String render(@NonNull final OffsetDateTime dateTime) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        final ZonedDateTime zonedDateTime = dateTime.atZoneSameInstant(ZoneId.systemDefault());

        if (zonedDateTime.toLocalDate().equals(now.toLocalDate())) {
            return "сегодня в " + zonedDateTime.format(TIME_FORMATTER);
        } else if (zonedDateTime.toLocalDate().equals(now.minusDays(1).toLocalDate())) {
            return "вчера в " + zonedDateTime.format(TIME_FORMATTER);
        } else {
            return zonedDateTime.format(DATE_TIME_FORMATTER);
        }
    }
}
