package backend.academy.scrapper.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import backend.academy.base.schema.scrapper.ListLinksResponse;
import backend.academy.base.schema.scrapper.RemoveLinkRequest;
import backend.academy.scrapper.ScrapperTest;
import backend.academy.scrapper.exception.LinkNotFoundException;
import backend.academy.scrapper.service.ScrapperService;
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
@WebMvcTest(SimpleScrapperController.class)
public class SimpleScrapperControllerTest implements ScrapperTest {

    @MockitoBean
    private final ScrapperService service = Mockito.mock(ScrapperService.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void registerChat__shouldReturn200_whenChatRegistered() throws Exception {
        // given-when
        mockMvc.perform(post("/tg-chat/123")).andExpect(status().isOk());
        // then
        verify(service).registerChat(123);
    }

    @Test
    public void deleteChat__shouldReturn200_whenChatDeleted() throws Exception {
        // given-when
        when(service.deleteChat(123)).thenReturn(true);
        mockMvc.perform(delete("/tg-chat/123")).andExpect(status().isOk());

        // then
        verify(service).deleteChat(123);
    }

    @Test
    public void deleteChat__shouldReturn404_whenChatNotFound() throws Exception {
        // given-when
        when(service.deleteChat(123)).thenReturn(false);
        mockMvc.perform(delete("/tg-chat/123")).andExpect(status().isNotFound());

        // then
        verify(service).deleteChat(123);
    }

    @Test
    public void getLinks__shouldReturn200_withLinks() throws Exception {
        // given
        final ListLinksResponse response = new ListLinksResponse(List.of(LINK_RESPONSE), 1);

        // when
        when(service.getLinks(123)).thenReturn(response.links());

        mockMvc.perform(get("/links").header("Tg-Chat-Id", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links", hasSize(1)))
                .andExpect(jsonPath("$.size").value(1));

        // then
        verify(service).getLinks(123);
    }

    @Test
    public void removeLink__shouldReturn404_whenLinkNotFound() throws Exception {
        // given
        final RemoveLinkRequest request = new RemoveLinkRequest(URL);

        // when
        when(service.removeLink(123, request.url())).thenThrow(new LinkNotFoundException(request.url()));

        mockMvc.perform(delete("/links")
                        .header("Tg-Chat-Id", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // then
        verify(service).removeLink(123, URL);
    }
}
