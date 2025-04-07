package backend.academy.bot.commands;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HelpBotCommandTest {

    private final HelpBotCommand help = new HelpBotCommand();
    private final int count = 15;

    @BeforeEach
    public void setUp() {
        Collections.nCopies(count, help).forEach(help::addCommandForDescription);
    }

    @Test
    public void message_shouldBeSplitOnNLines() {
        assertThat(help.handleRequest(1).message()).hasLineCount(count + 1);
    }
}
