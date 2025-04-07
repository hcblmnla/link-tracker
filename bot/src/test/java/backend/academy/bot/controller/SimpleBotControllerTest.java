package backend.academy.bot.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.BotTest;
import backend.academy.bot.sender.BotMessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SimpleBotController.class)
public class SimpleBotControllerTest implements BotTest {

    @MockitoBean
    private final BotMessageSender bot = Mockito.mock(BotMessageSender.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void sendUpdate__shouldReturn200_whenUpdateSent() throws Exception {
        final LinkUpdate update = new LinkUpdate(1L, URL, "", List.of(3L, 4L));

        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        verify(bot, times(1)).sendUpdate(eq(3L), any(), any());
        verify(bot, times(1)).sendUpdate(eq(4L), any(), any());
    }

    @Test
    public void sendUpdate__shouldReturn200_whenNoChatsProvided() throws Exception {
        final LinkUpdate update = new LinkUpdate(1L, URL, "", List.of());

        mockMvc.perform(post("/updates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        verify(bot, never()).sendUpdate(anyLong(), any(), any());
    }

    @Test
    public void sendUpdate__shouldReturn400_whenInvalidJsonProvided() throws Exception {
        final String invalidJson = "{ \"id\": 1, \"url\": [] }";

        mockMvc.perform(post("/updates").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
