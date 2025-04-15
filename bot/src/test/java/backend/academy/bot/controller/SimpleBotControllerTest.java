package backend.academy.bot.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.base.schema.bot.LinkUpdate;
import backend.academy.bot.BotTest;
import backend.academy.bot.link.service.LinkUpdateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private LinkUpdateService linkUpdateService;

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

        verify(linkUpdateService, times(1)).update(update);
    }

    @Test
    public void sendUpdate__shouldReturn200_whenNoChatsProvided() throws Exception {
        final LinkUpdate update = new LinkUpdate(1L, URL, "", List.of());

        mockMvc.perform(post("/updates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
            .andExpect(status().isOk());

        verify(linkUpdateService, times(1)).update(update);
    }

    @Test
    public void sendUpdate__shouldReturn400_whenInvalidJsonProvided() throws Exception {
        final String invalidJson = "{ \"id\": 1, \"url\": [] }";

        mockMvc.perform(post("/updates").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
            .andExpect(status().isBadRequest());
    }
}
